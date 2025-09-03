package com.typer.compete.infrastructure.repository

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.typer.auth.domain.repository.UserRepository
import com.typer.compete.domain.models.ContestCardModel
import com.typer.compete.domain.models.SubmissionModel
import com.typer.compete.domain.repository.CompeteRepository
import com.typer.core.either.Either
import com.typer.core.failure.Failure
import com.typer.core.failure.ServerFailure
import com.typer.create_contest.domain.models.ContestModel
import com.typer.create_contest.domain.models.ParticipantModel
import com.typer.create_contest.domain.models.toContestCardModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.ObjectId

class CompeteRepositoryImpl(
    mongoDatabase: MongoDatabase,
    private val userRepository: UserRepository
): CompeteRepository {
    companion object {
        private const val CONTEST_COLLECTION = "contests"
    }
    private val collection = mongoDatabase.getCollection(CONTEST_COLLECTION)

    override suspend fun getContestCards(userId: String): Either<Failure, List<ContestCardModel>> = withContext(Dispatchers.IO) {
        try {
            val res = collection.find(Filters.eq("players.userId", userId)).toList()
            if (res.isEmpty()) {
                return@withContext Either.Left(ServerFailure("User has no contests"))
            }
            return@withContext Either.Right(res.map {
                ContestModel.fromDocument(it).toContestCardModel(userId = userId)
            })
        } catch (e: Exception) {
            return@withContext Either.Left(ServerFailure("Failed to get user contests"))
        }
    }

    override suspend fun joinContestByCode(userId: String, contestCode: String): Either<Failure, ContestModel> = withContext(Dispatchers.IO) {
        try {
            val res = collection.find(Filters.eq("contestCode", contestCode)).first()
            if (res == null) {
                return@withContext Either.Left(ServerFailure("Contest not found"))
            }
            val contest = ContestModel.fromDocument(res)
            if (contest.status != "waiting") {
                return@withContext Either.Left(ServerFailure("Contest has already started or finished"))
            }
            if (contest.players.size >= contest.maxPlayers) {
                return@withContext Either.Left(ServerFailure("Contest is full"))
            }
            val user = userRepository.findByFirebaseId(userId)
            if (user !is Either.Right) {
                return@withContext Either.Left(ServerFailure("User not found"))
            }
            if (contest.players.any { it.userId == userId }) {
                return@withContext Either.Left(ServerFailure("User is already in contest"))
            }

            val updatedContest = contest.copy(
                players = contest.players.toMutableList().apply {
                    add(ParticipantModel(
                        userId = userId,
                        userName = user.value.name,
                        isCreator = false
                    ))
                }
            )

            val updateDoc = Document("\$set", updatedContest.toDocument(includeId = false))
            val updateRes = collection.updateOne(Filters.eq("_id", contest.id), updateDoc)
            if (updateRes.wasAcknowledged()) {
                return@withContext Either.Right(updatedContest)
            }
            return@withContext Either.Left(ServerFailure("Failed to join contest"))
        } catch (e: Exception) {
            return@withContext Either.Left(ServerFailure("Failed to join contest"))
        }
    }

    override suspend fun getContestByCode(contestCode: String): Either<Failure, ContestModel> = withContext(Dispatchers.IO) {
        try {
            val res = collection.find(Filters.eq("contestCode", contestCode)).first()
            if (res == null) {
                return@withContext Either.Left(ServerFailure("Contest not found"))
            }
            return@withContext Either.Right(ContestModel.fromDocument(res))

        } catch (e: Exception) {
            return@withContext Either.Left(ServerFailure("Failed to check contest"))
        }
    }

    override suspend fun finishContest(submissionModel: SubmissionModel): Either<Failure, Unit> {
        try {
            val res = collection.find(Filters.eq("_id", ObjectId(submissionModel.contestId))).first()
            if (res == null) {
                return Either.Left(ServerFailure("Contest not found"))
            }
            val contest = ContestModel.fromDocument(res)
            val newContest = contest.copy(players = contest.players.map {
                if (it.userId == submissionModel.userId) {
                    it.copy(wpm = submissionModel.wpm, accuracy = submissionModel.accuracy, hasFinished = true)
                } else {
                    it
                }
            })
            val updateDoc = Document("\$set", newContest.toDocument(includeId = false))
            val updateRes = collection.updateOne(Filters.eq("_id", contest.id), updateDoc)
            if (updateRes.wasAcknowledged()) {
                return Either.Right(Unit)
            }
            return Either.Left(ServerFailure("Failed to finish contest"))
        } catch (e: Exception) {
            return Either.Left(ServerFailure("Failed to finish contest${e.message}"))
        }
    }
}