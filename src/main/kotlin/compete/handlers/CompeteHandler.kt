package com.typer.compete.handlers

import com.typer.compete.domain.models.ContestCardModel
import com.typer.compete.domain.models.SubmissionModel
import com.typer.compete.domain.repository.CompeteRepository
import com.typer.core.either.Either
import com.typer.create_contest.domain.models.ContestModel
import com.typer.create_contest.domain.models.toContestCardModel
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
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class ContestCode(val code: String)
class CompeteHandler(
    private val bus: EventBus,
    private val repository: CompeteRepository
): WebSocketHandler {

    private val json = Json { ignoreUnknownKeys = true }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            bus.events.collect { event ->
                when (event) {
                    is WebsocketEvent.CreatedContest -> sendContestToOwner(event.session, event.userId, event.response)
                    else -> Unit
                }
            }
        }
    }
    override fun supportedTypes(): List<String> = listOf("getContests", "joiningContestWithCode", "contestInfoFromCode", "contestFinished")

    override suspend fun handle(
        session: WebSocketSession,
        userId: String,
        message: WebsocketMessage
    ) {
        try {
            when (message.type) {
                "getContests" -> handleGetContests(session, userId)
                "joiningContestWithCode" -> handleJoiningContestWithCode(session, userId, message)
                "contestInfoFromCode" -> handleContestInfoFromCode(session, message)
                "contestFinished" -> handleContestFinished(session, message)
            }
        } catch (e: Exception) {
            bus.publish(WebsocketEvent.SendError(session, "Invalid message format"))
        }
    }

    private suspend fun handleGetContests(session: WebSocketSession, userId: String) {
        try {
            val res = repository.getContestCards(userId)
            if (res is Either.Right) {
                bus.publish(WebsocketEvent.SendSuccess(session, "getContests",
                    Json.encodeToJsonElement(ListSerializer(ContestCardModel.serializer()), res.value)))
            } else if (res is Either.Left) {
                bus.publish(WebsocketEvent.SendError(session, res.error.message))
            }
        } catch (e: Exception) {
            bus.publish(WebsocketEvent.SendError(session, "Failed to get contests"))
        }
    }

    private suspend fun handleJoiningContestWithCode(session: WebSocketSession, userId: String, message: WebsocketMessage) {
        try {
            val contestCode = json.decodeFromJsonElement(ContestCode.serializer(),message.data!!).code
            val res = repository.joinContestByCode(userId, contestCode)
            if (res is Either.Right) {
                bus.publish(
                    WebsocketEvent.SendSuccess(
                        session, "joiningContestWithCode",
                        Json.encodeToJsonElement(ContestModel.serializer(), res.value)
                    )
                )
                bus.publish(WebsocketEvent.SendSuccess(session, "updatingAfterJoined",
                    Json.encodeToJsonElement(ContestCardModel.serializer(), res.value.toContestCardModel(userId))))
                bus.publish(WebsocketEvent.Broadcast("newParticipantJoinedWaiting",
                    json.encodeToJsonElement(res.value.players.find { it.userId == userId })))
                bus.publish(WebsocketEvent.Broadcast("newParticipantJoined",
                    json.encodeToJsonElement(mapOf(
                        "contestId" to res.value.id.toString()
                    ))))
            } else if (res is Either.Left) {
                bus.publish(WebsocketEvent.SendError(session, res.error.message))
            }
        } catch (e: Exception) {
            bus.publish(WebsocketEvent.SendError(session, "Failed to join contest ${e.message}"))
        }
    }

    private suspend fun handleContestInfoFromCode(session: WebSocketSession, message: WebsocketMessage) {
        try {
            val contestCode = json.decodeFromJsonElement(ContestCode.serializer(),message.data!!).code
            val res = repository.getContestByCode(contestCode)
            if (res is Either.Right) {
                bus.publish(
                    WebsocketEvent.SendSuccess(
                        session, "contestInfoFromCode",
                        Json.encodeToJsonElement(ContestModel.serializer(), res.value)
                    )
                )
            }
            else if (res is Either.Left) {
                bus.publish(WebsocketEvent.SendError(session, res.error.message))
            }
        } catch (e: Exception) {
            bus.publish(WebsocketEvent.SendError(session, "Failed to get contest info"))
        }
    }

    private suspend fun sendContestToOwner(session: WebSocketSession, userId: String, response: JsonElement?) {
        try {
            val contestCard = json.decodeFromJsonElement(ContestModel.serializer(), response!!).toContestCardModel(userId)
            bus.publish(WebsocketEvent.SendSuccess(session, "updatingAfterCreated", json.encodeToJsonElement(
                ContestCardModel.serializer(), contestCard)))
        } catch (e: Exception) {
            bus.publish(WebsocketEvent.SendError(session, "Failed to send contest to owner: ${e.message}"))
        }
    }

    private suspend fun handleContestFinished(session: WebSocketSession, message: WebsocketMessage) {
        try {
            val submissionModel = json.decodeFromJsonElement(SubmissionModel.serializer(), message.data!!)
            val res = repository.finishContest(submissionModel)
            when (res) {
                is Either.Left -> bus.publish(WebsocketEvent.SendError(session, res.error.message))
                is Either.Right -> {
                    bus.publish(WebsocketEvent.SendSuccess(session, "contestFinishedSuccess", null))
                    bus.publish(WebsocketEvent.Broadcast("userFinishedContest", json.encodeToJsonElement(submissionModel.userId)))
                }
            }
        } catch (e: Exception) {
            bus.publish(WebsocketEvent.SendError(session, "Failed to finish contest ${e.message}"))
        }
    }
}