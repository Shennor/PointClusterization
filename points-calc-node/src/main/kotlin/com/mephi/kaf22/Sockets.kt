package com.mephi.kaf22

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


fun Application.sockets(client: HttpClient, config: SlaveConfig) {
    launch {
        client.webSocket(HttpMethod.Get, host = "points-master", port = 8080, path = "/ws") {
            while (isActive) {
                val task = receiveDeserialized<Task>()
                val solution = this@sockets.solve(task);
                sendSerialized(solution)
            }
        }
    }
}

