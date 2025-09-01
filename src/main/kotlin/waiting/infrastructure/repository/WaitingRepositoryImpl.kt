package com.typer.waiting.infrastructure.repository

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.typer.auth.domain.repository.UserRepository
import com.typer.compete.domain.models.ContestCardModel
import com.typer.compete.domain.repository.CompeteRepository
import com.typer.core.either.Either
import com.typer.core.failure.AuthFailure
import com.typer.core.failure.Failure
import com.typer.core.failure.ServerFailure
import com.typer.create_contest.domain.models.ContestModel
import com.typer.create_contest.domain.models.ParticipantModel
import com.typer.create_contest.domain.models.toContestCardModel
import com.typer.waiting.domain.repository.WaitingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.ObjectId

class WaitingRepositoryImpl(
    mongoDatabase: MongoDatabase
): WaitingRepository {
    companion object {
        private const val CONTEST_COLLECTION = "contests"
    }
    private val collection = mongoDatabase.getCollection(CONTEST_COLLECTION)

    override suspend fun startContest(contestId: ObjectId, userId: String): Either<Failure, Unit> = withContext(Dispatchers.IO) {
        try {
            val res = collection.find(Filters.eq("_id", contestId)).first()
            if (res == null) {
                return@withContext Either.Left(ServerFailure("Contest not found"))
            }
            val contest = ContestModel.fromDocument(res)
            if (contest.status != "waiting") {
                return@withContext Either.Left(ServerFailure("Contest has already started or finished"))
            }
            if (contest.players.find { it.isCreator }?.userId != userId) {
                return@withContext Either.Left(AuthFailure("User is not the host"))
            }

            val updatedContest = contest.copy(status = "Started")

            val updateDoc = Document("\$set", updatedContest.toDocument(includeId = false))
            val updateRes = collection.updateOne(Filters.eq("_id", contest.id), updateDoc)
            if (updateRes.wasAcknowledged()) {
                return@withContext Either.Right(Unit)
            }
            return@withContext Either.Left(ServerFailure("Failed to join contest"))
        } catch (e: Exception) {
            return@withContext Either.Left(ServerFailure("Failed to join contest"))
        }
    }
}