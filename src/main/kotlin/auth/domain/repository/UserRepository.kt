package com.typer.auth.domain.repository

import com.typer.auth.domain.models.UserModel
import com.typer.core.either.Either
import com.typer.core.failure.Failure
import org.bson.types.ObjectId

interface UserRepository {
    suspend fun findOrInsert(user: UserModel): Either<Failure, UserModel>
    suspend fun findByFirebaseId(firebaseId: String): Either<Failure, UserModel>
    suspend fun findById(id: ObjectId): Either<Failure, UserModel>
}