package com.typer.websocket

import io.ktor.websocket.WebSocketSession
import kotlinx.serialization.json.JsonElement

sealed class WebsocketEvent {
    data class SendError(val session: WebSocketSession, val message: String) : WebsocketEvent()
    data class SendSuccess(val session: WebSocketSession, val type: String, val response: JsonElement?) : WebsocketEvent()
    data class CreatedContest(val session: WebSocketSession, val userId: String, val response: JsonElement?) : WebsocketEvent()
    data class Broadcast(val type: String, val response: JsonElement?) : WebsocketEvent()

    data class UserJoinedContest(val userId: String, val contestId: String) : WebsocketEvent()
    data class ContestUpdated(val contestId: String, val joinedCount: Int) : WebsocketEvent()
}
