package com.mephi.kaf22

import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
data class Point2(val x: Double, val y: Double) {
    fun squaredLengthTo(other: Point2): Double {
        return (x - other.x).pow(2) + (y - other.y).pow(2)
    }
    operator fun plus(other: Point2): Point2 = Point2(x + other.x, y + other.y)
}

@Serializable
data class Task(val centers: List<Point2>, val points: List<Point2>)

@Serializable
data class Solution(val mapping: Map<Point2, List<Point2>>)

data class ProcessedGroup(val oldCenter: Point2, val newCenter: Point2, val group: List<Point2>)

typealias ProcessedSolution = List<ProcessedGroup>

data class KMeanTask(val k: Int, val points: List<Point2>, val accuracy: Double)
