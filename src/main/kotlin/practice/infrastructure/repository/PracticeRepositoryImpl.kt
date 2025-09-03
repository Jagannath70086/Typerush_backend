package com.typer.practice.infrastructure.repository

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.typer.compete.domain.models.SubmissionModel
import com.typer.core.either.Either
import com.typer.core.failure.Failure
import com.typer.core.failure.ServerFailure
import com.typer.practice.domain.models.PracticeInfo
import com.typer.practice.domain.models.PracticeItemModel
import com.typer.practice.domain.repository.PracticeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId

class PracticeRepositoryImpl(
    mongoDatabase: MongoDatabase
): PracticeRepository {
    companion object {
        private const val PRACTICE_COLLECTION = "practiceItems"
        private const val SUBMISSION_COLLECTION = "practiceSubmissions"
    }
    private val collection = mongoDatabase.getCollection(PRACTICE_COLLECTION)
    private val submissionCollection = mongoDatabase.getCollection(SUBMISSION_COLLECTION)

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

    override suspend fun getPracticeInfoFromId(id: String): Either<Failure, PracticeInfo> = withContext(Dispatchers.IO) {
        try {
            val res = collection.find(Filters.eq("_id", ObjectId(id))).first()
            if (res == null) {
                return@withContext Either.Left(ServerFailure("No practice item found"))
            }
            return@withContext Either.Right(PracticeInfo.fromDocument(res))
        } catch (e: Exception) {
            println(e.message)
            return@withContext Either.Left(ServerFailure("Failed to get practice info ${e.message}"))
        }
    }

    override suspend fun submitPracticeResults(submissionModel: SubmissionModel): Either<Failure, String> = withContext(Dispatchers.IO){
        try {
            val res = submissionCollection.insertOne(submissionModel.toDocument())
            if (res.wasAcknowledged()) {
                return@withContext Either.Right("Practice results submitted successfully")
            }
            return@withContext Either.Left(ServerFailure("Failed to submit practice results"))
        } catch (e: Exception) {
            println(e.message)
            return@withContext Either.Left(ServerFailure("Failed to submit practice results"))
        }
    }
}