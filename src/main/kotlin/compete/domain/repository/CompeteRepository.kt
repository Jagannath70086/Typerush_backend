package com.typer.compete.domain.repository

import com.typer.compete.domain.models.ContestCardModel
import com.typer.compete.domain.models.SubmissionModel
import com.typer.core.either.Either
import com.typer.core.failure.Failure
import com.typer.create_contest.domain.models.ContestModel

interface CompeteRepository {
    suspend fun getContestCards(userId: String): Either<Failure, List<ContestCardModel>>
    suspend fun joinContestByCode(userId: String, contestCode: String): Either<Failure, ContestModel>
    suspend fun getContestByCode(contestCode: String): Either<Failure, ContestModel>
    suspend fun finishContest(submissionModel: SubmissionModel): Either<Failure, Unit>

}