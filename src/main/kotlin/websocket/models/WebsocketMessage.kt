package com.typer.websocket.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class WebsocketMessage(
    val type: String,
    val data: JsonElement? = null
)
