package com.typer.create_contest.domain.repository

import com.typer.core.either.Either
import com.typer.core.failure.Failure
import com.typer.create_contest.domain.models.ContestModel

interface CreateContestRepository {
    suspend fun createContest(userId: String, contest: ContestModel): Either<Failure, ContestModel>
}