package com.mephi.kaf22

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.cbor.*
import io.ktor.server.application.*
import kotlinx.serialization.cbor.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configuration()

    val client = HttpClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Cbor)
        }
//        install(ContentNegotiation) {
//            cbor(Cbor {
//                ignoreUnknownKeys = true
//            })
//        }
    }

    val text = File("slave.conf").readText()
    val config = Json.decodeFromString<SlaveConfig>(text)

    sockets(client, config)
}
