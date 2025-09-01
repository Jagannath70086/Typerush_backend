package com.typer.websocket.routes

import com.typer.auth.domain.models.FirebaseUser
import com.typer.websocket.WebsocketManager
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import org.koin.ktor.ext.inject

fun Route.websocketRoutes() {
    val socketManager by inject<WebsocketManager>()

    authenticate("firebase") {
        webSocket("/contest") {
            val firebaseUser = call.principal<FirebaseUser>()!!
            socketManager.handleConnection(this, firebaseUser.uid)
        }
    }
}
