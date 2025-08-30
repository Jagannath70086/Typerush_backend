package com.typer.core.failure

interface Failure {
    val message: String
}


class AuthFailure(override val message: String): Failure

class ServerFailure(override val message: String): Failure

class SocketFailure(override val message: String): Failure

class UnexpectedFailure(override val message: String): Failure