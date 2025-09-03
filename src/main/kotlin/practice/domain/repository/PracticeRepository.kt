package com.typer.practice.domain.repository

import com.typer.compete.domain.models.SubmissionModel
import com.typer.core.either.Either
import com.typer.core.failure.Failure
import com.typer.practice.domain.models.PracticeInfo
import com.typer.practice.domain.models.PracticeItemModel

interface PracticeRepository {
    suspend fun getPracticeItem(): Either<Failure, List<PracticeItemModel>>
    suspend fun getPracticeInfoFromId(id: String): Either<Failure, PracticeInfo>
    suspend fun submitPracticeResults(submissionModel: SubmissionModel): Either<Failure, String>
}