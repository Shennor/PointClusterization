package com.mephi.kaf22

import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
data class Point2(val x: Double, val y: Double) {
    fun squaredLengthTo(other: Point2): Double {
        return (x - other.x).pow(2) + (y - other.y).pow(2)
    }
}

@Serializable
data class Task(val centers: List<Point2>, val points: List<Point2>)

@Serializable
data class Solution(val mapping: Map<Point2, List<Point2>>)

@Serializable
data class SlaveConfig(val masterUrl: String)