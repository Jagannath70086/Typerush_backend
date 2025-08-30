package com.typer.auth.infrastructure.repository

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.typer.auth.domain.models.UserModel
import com.typer.auth.domain.repository.UserRepository
import com.typer.core.either.Either
import com.typer.core.failure.Failure
import com.typer.core.failure.ServerFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId

class UserRepositoryImpl(
    mongoDatabase: MongoDatabase
): UserRepository {
    companion object {
        private const val USER_COLLECTION = "users"
    }
    private val collection = mongoDatabase.getCollection(USER_COLLECTION)

    override suspend fun findOrInsert(user: UserModel): Either<Failure, UserModel> = withContext(Dispatchers.IO) {
        try {
            val existingUser = collection.find(Filters.eq("firebaseId", user.firebaseId)).first()
            if (existingUser != null) {
                return@withContext Either.Right(UserModel.fromDocument(existingUser))
            }

            val result = collection.insertOne(user.toDocument())
            if (result.wasAcknowledged()) {
                return@withContext Either.Right(user.copy(id = result.insertedId?.asObjectId()?.value))
            }
            return@withContext Either.Left(ServerFailure("Error inserting user"))
        } catch (e: Exception) {
            return@withContext Either.Left(ServerFailure("Error inserting user: ${e.message}"))
        }
    }

    override suspend fun findByFirebaseId(firebaseId: String): Either<Failure, UserModel> = withContext(Dispatchers.IO) {
        try {
            val document = collection.find(Filters.eq("firebaseId", firebaseId)).first()
            if (document == null) {
                return@withContext Either.Left(ServerFailure("User not found"))
            } else {
                return@withContext Either.Right(UserModel.fromDocument(document))
            }
        } catch (e: Exception) {
            return@withContext Either.Left(ServerFailure("Error finding user by firebase ID: ${e.message}"))
        }
    }

    override suspend fun findById(id: ObjectId): Either<Failure, UserModel> = withContext(Dispatchers.IO) {
        return@withContext try {
            val document = collection.find(Filters.eq("_id", id)).first()
            document?.let { Either.Right(UserModel.fromDocument(it)) }
                ?: Either.Left(ServerFailure("User not found"))
        } catch (e: Exception) {
            Either.Left(ServerFailure("Error finding user by ID: ${e.message}"))
        }
    }
}