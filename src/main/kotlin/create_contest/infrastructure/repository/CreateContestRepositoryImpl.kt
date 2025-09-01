package com.typer.create_contest.infrastructure.repository

import com.mongodb.client.MongoDatabase
import com.typer.auth.domain.models.toParticipantModel
import com.typer.auth.domain.repository.UserRepository
import com.typer.core.either.Either
import com.typer.core.failure.Failure
import com.typer.core.failure.ServerFailure
import com.typer.create_contest.domain.models.ContestModel
import com.typer.create_contest.domain.repository.CreateContestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateContestRepositoryImpl(
    mongoDatabase: MongoDatabase,
    private val userRepository: UserRepository
): CreateContestRepository {
    companion object {
        private const val CONTEST_COLLECTION = "contests"
    }
    private val collection = mongoDatabase.getCollection(CONTEST_COLLECTION)

    private fun generateContestCode(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..6)
            .map { allowedChars.random() }
            .joinToString("")
            .uppercase()
    }

    override suspend fun createContest(
        userId: String,
        contest: ContestModel
    ): Either<Failure, ContestModel> = withContext(Dispatchers.IO) {
        try {
            val contestCode = generateContestCode()
            val userRes = userRepository.findByFirebaseId(userId)
            if (userRes is Either.Left) {
                return@withContext Either.Left(ServerFailure("User not found"))
            }
            val participant = (userRes as Either.Right).value.toParticipantModel(isCreator = true)
            val updatedContest = contest.copy(contestCode = contestCode, players = listOf(participant))
            val res = collection.insertOne(updatedContest.toDocument())

            if(res.wasAcknowledged()) {
                return@withContext Either.Right(ContestModel.fromDocument(updatedContest.copy(id = res.insertedId?.asObjectId()?.value, contestCode = contestCode).toDocument()))
            }
            return@withContext Either.Left(ServerFailure("Failed to create contest"))
        } catch (e: Exception) {
            return@withContext Either.Left(ServerFailure("Error creating contest: ${e.message}"))
        }
    }
}