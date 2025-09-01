package com.typer.create_contest.handlers

import com.typer.core.either.Either
import com.typer.create_contest.domain.models.ContestModel
import com.typer.create_contest.domain.repository.CreateContestRepository
import com.typer.websocket.WebSocketHandler
import com.typer.websocket.WebsocketEvent
import com.typer.websocket.inter_data_exchange.EventBus
import com.typer.websocket.models.WebsocketMessage
import io.ktor.websocket.WebSocketSession
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class CreateContestHandler(
    private val bus: EventBus,
    private val repository: CreateContestRepository
): WebSocketHandler {

    private val json = Json { ignoreUnknownKeys = true }

    override fun supportedTypes(): List<String> = listOf("createContest")

    override suspend fun handle(
        session: WebSocketSession,
        userId: String,
        message: WebsocketMessage
    ) {
        try {
            when (message.type) {
                "createContest" -> createContest(session, userId, message)
            }
        } catch (e: Exception) {
            bus.publish(WebsocketEvent.SendError(session, "Invalid message format"))
        }
    }

    private suspend fun createContest(session: WebSocketSession, userId: String, message: WebsocketMessage) {
        try {
            val contest = json.decodeFromJsonElement(ContestModel.serializer(),message.data!!)
            val res = repository.createContest(userId = userId, contest = contest)
            if (res is Either.Left) {
                bus.publish(WebsocketEvent.SendError(session, res.error.message))
                return
            }
            bus.publish(WebsocketEvent.CreatedContest(session, userId, json.encodeToJsonElement((res as Either.Right).value)))
            bus.publish(WebsocketEvent.SendSuccess(session, "createdContest", json.encodeToJsonElement(
                res.value)))
        } catch (e: Exception) {
            bus.publish(WebsocketEvent.SendError(session, "Failed to create contest"))
        }
    }
}