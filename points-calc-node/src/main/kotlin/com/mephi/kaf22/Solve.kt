package com.mephi.kaf22

import io.ktor.server.application.*

fun Application.solve(task: Task): Solution {
    val mapping = task.points.map { point ->
        val center = task
            .centers
            .map { it to it.squaredLengthTo(point) }
            .minBy { it.second }
            .first
        center to point
    }.groupBy({ it.first }, { it.second })

    log.debug(mapping.toString())

    return Solution(mapping)
}