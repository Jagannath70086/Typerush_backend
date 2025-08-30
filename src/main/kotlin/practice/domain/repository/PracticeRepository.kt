package com.typer.practice.domain.repository

import com.typer.core.either.Either
import com.typer.core.failure.Failure
import com.typer.practice.domain.models.PracticeItemModel

interface PracticeRepository {
    suspend fun getPracticeItem(): Either<Failure, List<PracticeItemModel>>
}