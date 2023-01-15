package com.mephi.kaf22

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

fun parsePoints(path: String, accuracy: Double): KMeanTask {
    val lines = File(path).readLines()
    val k = lines.first().toInt()
    val points = lines.drop(1)
        .map { Json.decodeFromString<Point2>(it) }
    return KMeanTask(k, points, accuracy)
}