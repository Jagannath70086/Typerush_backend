package com.typer.auth.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseUser(
    val uid: String,
    val name: String?,
    val email: String?,
    val picture: String?
)

fun FirebaseUser.toUser(): UserModel {
    return UserModel(
        firebaseId = uid,
        name = name ?: "Anonymous",
        email = email ?: "",
        photoUrl = picture ?: ""
    )
}