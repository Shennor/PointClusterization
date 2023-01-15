package com.mephi.kaf22

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.cbor.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import jetbrains.datalore.base.math.ipow
import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.geom.geom_point
import jetbrains.letsPlot.ggplot
import jetbrains.letsPlot.letsPlot
import jetbrains.letsPlot.scale.scaleColorManual
import jetbrains.letsPlot.scale.scale_color_manual
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.Color
import java.io.File
import kotlin.math.roundToInt

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)


@OptIn(ExperimentalSerializationApi::class)
@Suppress("unused")
// application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configuration()

    routing {
        val sessionSet: Sessions = ConcurrentSet()

        get("/solve") {
            val path = call.request.queryParameters["path"] ?: return@get
            val accuracy = call.request.queryParameters["accuracy"]?.toDouble() ?: 0.1
            val kMeanTask = parsePoints(path, accuracy)
            val solution = kMeanTask.solveKMeanTask(sessionSet)
            val file = plotClusters(solution.map { it.group })
            call.respondFile(File(file))
        }


        webSocket("/ws") {
            val s = Session(this)
            sessionSet.add(s)
            s.init()
            sessionSet.remove(s)
        }
    }
}

data class Session(val socket: WebSocketServerSession)
    : WebSocketServerSession by socket
{
    private val exit: CompletableDeferred<Boolean> = CompletableDeferred()
    suspend fun init(): Session {
        exit.await()
        return this
    }
}

typealias Sessions = MutableSet<Session>

suspend fun KMeanTask.solveKMeanTask(solvers: Sessions): ProcessedSolution {
    val initialCenters = points.run {
        (1..k).map { random() }
    }
    val solution = iteration(initialCenters, solvers)
    return solution
}

private tailrec suspend fun KMeanTask.iteration(
    currentCenters: List<Point2>,
    solvers: Sessions
): ProcessedSolution {
    val chunkSize = (points.size.toDouble() / solvers.size).roundToInt() + 1
    val groups = points.chunked(chunkSize)

    val tasks = groups.map { Task(currentCenters, it) }
    val solutions = tasks.zip(solvers).map {
        it.second.sendTask(it.first)
    }
    val solution = mergeSolutions(solutions)

    val pSolution = calculateCenters(solution)

    return if (pSolution
            .map { it.oldCenter.squaredLengthTo(it.newCenter) }
            .all { it < accuracy }
    ) pSolution

    else iteration(pSolution.map { it.newCenter }, solvers)
}



fun mergeSolutions(solutions: List<Solution>): Solution {
    val mutableMap = mutableMapOf<Point2, List<Point2>>()
    for (sol in solutions) {
        for (entry in sol.mapping.entries) {
            mutableMap.merge(entry.key, entry.value, List<Point2>::plus)
        }
    }
    return Solution(mutableMap)
}



fun calculateCenters(solution: Solution): ProcessedSolution =
    solution.mapping.entries.map {
        val sum = it.value.reduce(Point2::plus)
        val mean = Point2(sum.x / it.value.size, sum.y / it.value.size)
        ProcessedGroup(it.key, mean, it.value)
    }

suspend fun WebSocketServerSession.sendTask(task: Task): Solution {
    sendSerialized(task)
    return receiveDeserialized()
}

fun plotClusters(clusters: List<List<Point2>>): String {
    val data = clusters.flatMapIndexed { index, cluster ->
        cluster.map { point ->
            mapOf("x" to point.x, "y" to point.y, "cluster" to index)
        }
    }
    val data1 = makeClusters(data)

    val plot = ggplot(data1) { x = "x"; y = "y" } +
            geomPoint(size = 4.0) { color = "cluster" }
//            scaleColorManual(values = (clusters.indices).map { it })

    return ggsave(plot, "plot.html")
}
fun <A, B> makeClusters(data: List<Map<A, B>>): Map<A, List<B>> {
    val clusters = mutableMapOf<A, MutableList<B>>()
    data.forEach { item ->
        item.keys.map { key ->
            val value = item[key]
            if (clusters.containsKey(key)) {
                clusters[key]?.add(value!!)
            } else {
                clusters[key] = mutableListOf(value!!)
            }
        }
    }
    return clusters
}