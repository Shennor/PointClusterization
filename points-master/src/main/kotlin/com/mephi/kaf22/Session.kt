package com.mephi.kaf22

import io.ktor.server.websocket.*
import kotlinx.coroutines.CompletableDeferred

data class Session(val socket: WebSocketServerSession) : WebSocketServerSession by socket {
    private val exit: CompletableDeferred<Boolean> = CompletableDeferred()
    suspend fun init(): Session {
        exit.await()
        return this
    }
}
typealias Sessions = MutableSet<Session>

suspend fun WebSocketServerSession.sendTask(task: Task): Solution {
    sendSerialized(task)
    return receiveDeserialized()
}