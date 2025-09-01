package com.typer.waiting.domain.repository

import com.typer.compete.domain.models.ContestCardModel
import com.typer.core.either.Either
import com.typer.core.failure.Failure
import com.typer.create_contest.domain.models.ContestModel
import org.bson.types.ObjectId

interface WaitingRepository {
    suspend fun startContest(contestId: ObjectId, userId: String): Either<Failure, Unit>
}