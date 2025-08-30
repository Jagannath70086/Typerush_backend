package com.typer.core.config

import com.kborowy.authprovider.firebase.firebase
import com.typer.auth.domain.models.FirebaseUser
import io.ktor.server.application.*
import io.ktor.server.application.install
import io.ktor.server.auth.*
import kotlin.io.path.createTempFile
import kotlin.io.writeText

fun Application.setupFirebaseAuth() {
    install(Authentication) {
        firebase("firebase") {
            setup {
                val firebaseAdminContent = System.getProperty("FIREBASE_SERVICE_ACCOUNT")
                    ?: throw kotlin.IllegalStateException("Either FIREBASE_SERVICE_ACCOUNT or FIREBASE_SERVICE_ACCOUNT must be set")

                val tempFile = createTempFile("firebase-admin", ".json").toFile()
                tempFile.writeText(firebaseAdminContent)
                adminFile = tempFile
                tempFile.deleteOnExit()
            }
            realm = "My Server"
            validate { token ->
                FirebaseUser(
                    uid = token.uid,
                    name = token.name,
                    email = token.email,
                    picture = token.picture
                )
            }
        }
    }
}