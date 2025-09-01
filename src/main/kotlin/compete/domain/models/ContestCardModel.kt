package com.typer.compete.domain.models

import com.typer.core.serialization.ObjectIdSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId

@Serializable
data class ContestCardModel(
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId,
    val title: String,
    val authorName: String,
    val contestCode: String,
    val status: String,
    val tags: List<String>,
    val time: Int,
    val currentPlayers: Int,
    val totalPlayers: Int,
    val isMine: Boolean
) {
    fun toDocument(): Document {
        val jsonString = Json.encodeToString(this)
        val document = Document.parse(jsonString)

        document["_id"] = id

        return document
    }

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
        }

        fun fromDocument(document: Document): ContestCardModel {
            val mutableDoc = Document(document)
            document["_id"]?.let { objectId ->
                if (objectId is ObjectId) {
                    mutableDoc["_id"] = objectId.toHexString()
                }
            }

            return json.decodeFromString(mutableDoc.toJson())
        }
    }
}

