package com.typer.websocket

import com.typer.websocket.inter_data_exchange.EventBus
import com.typer.websocket.models.ErrorResponse
import com.typer.websocket.models.SuccessResponse
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import com.typer.websocket.models.WebsocketMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

class WebsocketManager(
    handlers: List<WebSocketHandler>,
    private val eventBus: EventBus
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val userSessions = ConcurrentHashMap<WebSocketSession, String>()

    private val handlerRegistry: Map<String, WebSocketHandler> =
        handlers.flatMap { it.supportedTypes().map { type -> type to it } }.toMap()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            eventBus.events.collect { event ->
                when (event) {
                    is WebsocketEvent.SendError -> sendError(event.session, event.message)
                    is WebsocketEvent.SendSuccess -> sendSuccess(event.session, event.type,event.response)
                    is WebsocketEvent.Broadcast -> broadcast(event.type, event.response)
                    else -> Unit
                }
            }
        }
    }

    suspend fun handleConnection(session: WebSocketSession, userId: String) {
        userSessions[session] = userId

        try {
            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val messageText = frame.readText()
                        val message = json.decodeFromString<WebsocketMessage>(messageText)
                        val handler = handlerRegistry[message.type]

                        if (handler != null) {
                            handler.handle(session, userId, message)
                        } else {
                            sendError(session, "Unknown message type: ${message.type}")
                        }
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            println("Websocket error for user $userId: ${e.message}")
        } finally {
            handleDisconnection(session, userId)
        }
    }

    private fun handleDisconnection(session: WebSocketSession, userId: String) {
        userSessions.remove(session)
    }

    private suspend fun sendError(session: WebSocketSession, msg: String) {
        val error = WebsocketMessage("error", json.encodeToJsonElement(ErrorResponse(msg)))
        session.send(Frame.Text(json.encodeToString(error)))
    }

    private suspend fun sendSuccess(session: WebSocketSession, type: String, response: JsonElement?) {
        val successMessage = json.encodeToJsonElement(WebsocketMessage(type, json.encodeToJsonElement(
            SuccessResponse(
                "Success",
                response
            )
        )))
        session.send(Frame.Text(json.encodeToString(successMessage)))
    }

    private suspend fun broadcast(type: String, response: JsonElement?) {
        for (session in userSessions.keys) {
            sendSuccess(session, type, response)
        }
    }
}
