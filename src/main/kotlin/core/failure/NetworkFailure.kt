package com.typer.typerush.core.failure

import com.typer.core.failure.Failure

class NetworkFailure(error: NetworkError): Failure {
    override val message: String = when(error) {
        NetworkError.REQUEST_TIMEOUT -> "Request timeout"
        NetworkError.TOO_MANY_REQUESTS -> "Too many requests"
        NetworkError.NO_INTERNET_CONNECTION -> "No internet connection"
        NetworkError.SERVER_ERROR -> "Server error"
        NetworkError.SERIALIZATION -> "Serialization error"
        NetworkError.UNKNOWN -> "Unknown error"
    }
}

enum class NetworkError {
    REQUEST_TIMEOUT,
    TOO_MANY_REQUESTS,
    NO_INTERNET_CONNECTION,
    SERVER_ERROR,
    SERIALIZATION,
    UNKNOWN
}