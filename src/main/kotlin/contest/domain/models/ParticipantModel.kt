package com.typer.contest.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ParticipantModel(
    val userId: String,
    val userName: String,
    val isCreator: Boolean = false,
    val progress: Int = 0,
    val wpm: Int = 0,
    val accuracy: Double = 0.0,
    val hasFinished: Boolean = false
)