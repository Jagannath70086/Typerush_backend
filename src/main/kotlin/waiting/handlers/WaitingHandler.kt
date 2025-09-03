package com.typer.waiting.handlers

import com.typer.compete.domain.models.ContestCardModel
import com.typer.core.either.Either
import com.typer.create_contest.domain.models.ContestModel
import com.typer.create_contest.domain.models.toContestCardModel
import com.typer.create_contest.domain.models.toGameInfoModel
import com.typer.waiting.domain.models.GameInfoModel
import com.typer.waiting.domain.repository.WaitingRepository
import com.typer.websocket.WebSocketHandler
import com.typer.websocket.WebsocketEvent
import com.typer.websocket.inter_data_exchange.EventBus
import com.typer.websocket.models.WebsocketMessage
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import org.bson.types.ObjectId

@Serializable
data class ContestIdToStart(val contestId: String)
class WaitingHandler(
    private val bus: EventBus,
    private val repository: WaitingRepository
): WebSocketHandler {

    private val json = Json { ignoreUnknownKeys = true }

    override fun supportedTypes(): List<String> = listOf("startRoomById")

    override suspend fun handle(
        session: WebSocketSession,
        userId: String,
        message: WebsocketMessage
    ) {
        try {
            when (message.type) {
                "startRoomById" -> handleStartRoom(session, userId, message)
            }
        } catch (e: Exception) {
            bus.publish(WebsocketEvent.SendError(session, "Invalid message format"))
        }
    }

    private suspend fun handleStartRoom(session: WebSocketSession, userId: String, message: WebsocketMessage) {
        try {
            val contestId = json.decodeFromJsonElement<ContestIdToStart>(message.data!!).contestId
            val contestIdAsObjectId = ObjectId(contestId)
            val res = repository.startContest(contestIdAsObjectId, userId)
            if (res is Either.Right) {
                bus.publish(WebsocketEvent.Broadcast( "contestStartSuccess", json.encodeToJsonElement(
                    GameInfoModel.serializer(),
                    res.value.toGameInfoModel()
                )))
            } else if (res is Either.Left) {
                bus.publish(WebsocketEvent.SendError(session, res.error.message))
            }
        } catch (e: Exception) {
            bus.publish(WebsocketEvent.SendError(session, "Failed to get contests"))
        }
    }
}