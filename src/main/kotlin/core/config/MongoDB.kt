package com.typer.core.config

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import kotlin.let
import kotlin.text.orEmpty
import kotlin.text.toInt

fun Application.connectToMongoDB(): MongoDatabase {
    val user = System.getProperty("MONGO_USER")
    val password = System.getProperty("MONGO_PASSWORD")
    val host = System.getProperty("MONGO_HOST") ?: "127.0.0.1"
    val port = System.getProperty("MONGO_PORT") ?: "27017"
    val maxPoolSize = System.getProperty("MONGO_MAX_POOL_SIZE")?.toInt() ?: 20
    val databaseName = System.getProperty("MONGO_DATABASE_NAME") ?: "userDb"

    val credentials = user?.let { userVal -> password?.let { passwordVal -> "$userVal:$passwordVal@" } }.orEmpty()
    val uri = "mongodb+srv://$credentials$host/?maxPoolSize=$maxPoolSize&w=majority"

    val mongoClient = MongoClients.create(uri)
    val database = mongoClient.getDatabase(databaseName)

    println("Connected to MongoDB: uri= $uri")

    monitor.subscribe(ApplicationStopped) {
        mongoClient.close()
    }

    return database
}