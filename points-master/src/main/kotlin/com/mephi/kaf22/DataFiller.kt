package com.mephi.kaf22

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object DataFiller {
    @JvmStatic
    fun main(args: Array<String>) {
        val file = File("./data/testData3.txt")
        val writer = file.writer()
        val random = kotlin.random.Random(42)
        writer.appendLine("7")
        (1..999).map {
            Point2(
                random.nextDouble(-10000.0, 10000.0),
                random.nextDouble(-10000.0, 10000.0),
            )
        }.forEach {
            writer.appendLine(Json.encodeToString(it))
        }
        writer.close()
    }
}