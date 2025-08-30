package com.typer

import com.typer.core.config.configureCORS
import com.typer.core.config.configureRouting
import com.typer.core.config.configureSockets
import com.typer.core.config.di
import com.typer.core.config.setupFirebaseAuth
import io.github.cdimascio.dotenv.dotenv
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun main(args: Array<String>) {
    val dotenv = dotenv()
    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    di()
    install(ContentNegotiation) {
        json()
    }
    configureCORS()
    setupFirebaseAuth()
    configureSockets()
    configureRouting()
}
