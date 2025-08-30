package com.typer.core.config

import com.typer.core.di.appModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.di() {
    val mongoDatabase = connectToMongoDB()

    val databaseModule = module {
        single { mongoDatabase }
    }

    install(Koin) {
        slf4jLogger()
        modules(
            databaseModule,
            appModule
        )
    }
}