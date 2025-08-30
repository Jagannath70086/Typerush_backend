package com.typer.practice.routes

import com.typer.core.either.Either
import com.typer.practice.domain.repository.PracticeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import kotlin.to

fun Route.practiceItemRoutes() {
    val practiceRepository by inject<PracticeRepository>()

    route("/api/type-items") {
        authenticate("firebase") {
            get {
                val res = practiceRepository.getPracticeItem()
                if (res is Either.Right) {
                    call.respond(res.value)
                } else {
                    call.respond(HttpStatusCode.InternalServerError,
                        mapOf("error" to "Can't get practice items")
                    )
                }
            }
        }
    }
}