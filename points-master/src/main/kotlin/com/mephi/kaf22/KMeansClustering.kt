package com.mephi.kaf22

import kotlin.math.roundToInt

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