package com.typer.practice.infrastructure.repository

import com.mongodb.client.MongoDatabase
import com.typer.core.either.Either
import com.typer.core.failure.Failure
import com.typer.core.failure.ServerFailure
import com.typer.practice.domain.models.PracticeItemModel
import com.typer.practice.domain.repository.PracticeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PracticeRepositoryImpl(
    mongoDatabase: MongoDatabase
): PracticeRepository {
    companion object {
        private const val PRACTICE_COLLECTION = "practiceItems"
    }
    private val collection = mongoDatabase.getCollection(PRACTICE_COLLECTION)
    override suspend fun getPracticeItem(): Either<Failure, List<PracticeItemModel>> = withContext(Dispatchers.IO) {
        try {
            val res = collection.find().toList()
            if (res.isEmpty()) {
                return@withContext Either.Left(ServerFailure("No practice items found"))
            }
            return@withContext Either.Right(res.map { PracticeItemModel.fromDocument(it) })
        } catch (e: Exception) {
            return@withContext Either.Left(ServerFailure("Failed to get practice items"))
        }
    }
}