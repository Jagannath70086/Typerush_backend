package com.typer.websocket

import com.typer.websocket.models.WebsocketMessage
import io.ktor.websocket.WebSocketSession

interface WebSocketHandler {
    fun supportedTypes(): List<String>
    suspend fun handle(session: WebSocketSession, userId: String, message: WebsocketMessage)
}