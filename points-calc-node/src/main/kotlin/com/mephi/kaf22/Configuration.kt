package com.mephi.kaf22

import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.cbor.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.websocket.*
import kotlinx.serialization.cbor.Cbor
import java.time.Duration


fun Application.configuration() {

//    install(ContentNegotiation) {
//        cbor()
//    }

}