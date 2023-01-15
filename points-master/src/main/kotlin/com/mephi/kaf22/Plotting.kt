package com.mephi.kaf22

import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.ggplot

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