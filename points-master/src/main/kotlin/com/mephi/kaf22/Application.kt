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
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File

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


