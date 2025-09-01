package com.typer.create_contest.domain.models

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantModel(
    val userId: String,
    val userName: String,
    @EncodeDefault
    val isCreator: Boolean = false,
    @EncodeDefault
    val progress: Int = 0,
    @EncodeDefault
    val wpm: Int = 0,
    @EncodeDefault
    val accuracy: Double = 0.0,
    @EncodeDefault
    val hasFinished: Boolean = false
)