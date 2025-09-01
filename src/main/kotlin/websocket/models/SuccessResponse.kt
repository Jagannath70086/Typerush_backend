package com.typer.websocket.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SuccessResponse(
    val message: String,
    val response: JsonElement? = null
)
