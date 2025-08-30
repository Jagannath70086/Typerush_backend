package com.typer.auth.routes

import com.typer.auth.domain.repository.UserRepository
import com.typer.core.either.Either
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import kotlin.to
import com.typer.auth.domain.models.FirebaseUser
import com.typer.auth.domain.models.toUser
import com.typer.auth.domain.models.toUserResponseModel

fun Route.userRoutes() {
    val repository by inject<UserRepository>()

    route("/api/auth") {
        authenticate("firebase") {
            post("/login") {
                val firebaseUser = call.principal<FirebaseUser>()!!
                val res = repository.findOrInsert(firebaseUser.toUser())
                if (res is Either.Right) {
                    call.respond(res.value.toUserResponseModel())
                } else {
                    call.respond(HttpStatusCode.Unauthorized,
                        mapOf("error" to "Unauthorized")
                    )
                }
            }
            get("/me") {
                val firebaseUser = call.principal<FirebaseUser>()!!
                val res = repository.findByFirebaseId(firebaseUser.uid)
                if (res is Either.Right) {
                    call.respond(res.value.toUserResponseModel())
                } else {
                    call.respond(HttpStatusCode.Unauthorized,
                        mapOf("error" to "Unauthorized")
                    )
                }
            }
        }
    }
}