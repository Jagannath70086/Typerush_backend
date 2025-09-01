package com.typer.core.config

import com.typer.auth.routes.userRoutes
import com.typer.practice.routes.practiceItemRoutes
import com.typer.websocket.routes.websocketRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        userRoutes()
        practiceItemRoutes()
        websocketRoutes()
    }
}
