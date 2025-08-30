package com.typer.core.either

import com.typer.core.failure.Failure

typealias Error = Failure
sealed interface Either<out A: Failure, out B> {
    data class Left<out A: Failure>(val error: A): Either<A, Nothing>
    data class Right<out B>(val value: B): Either<Nothing, B>
}

inline fun <A: Error, B, C> Either<A, B>.map(map: (B) -> C): Either<A, C> {
    return when(this) {
        is Either.Left -> Either.Left(error)
        is Either.Right -> Either.Right(map(value))
    }
}

fun <A: Error, B> Either<A, B>.asEmptyDataResult(): EmptyResult<A> {
    return map {}
}

inline fun <A: Error, B> Either<A, B>.onSuccess(action: (B) -> Unit): Either<A, B> {
    return when (this) {
        is Either.Left -> this
        is Either.Right -> {
            action(value)
            this
        }
    }
}

inline fun <A: Error, B> Either<A, B>.onError(action: (A) -> Unit): Either<A, B> {
    return when (this) {
        is Either.Left -> {
            action(error)
            this
        }
        is Either.Right -> this
    }
}

typealias EmptyResult<A> = Either<A, Unit>