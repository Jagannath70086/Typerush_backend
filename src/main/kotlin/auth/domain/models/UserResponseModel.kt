package com.typer.auth.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponseModel(
    @SerialName("_id")
    val id: String? = null,
    val name: String = "Anonymous",
    val email: String = "abc@example.com",
    val photoUrl: String = "",
)