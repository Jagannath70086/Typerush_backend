package com.typer.contest.domain.models

import com.typer.compete.domain.models.ContestCardModel
import com.typer.core.serialization.ObjectIdSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId

@Serializable
data class ContestModel(
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId? = null,
    val title: String,
    val contestCode: String? = null,
    val textSnippet: String,
    val duration: Int,
    val remainingTime: Long? = null,
    val maxPlayers: Int = 100,
    val status: String = "waiting",
    val tags: List<String> = listOf(),
    val difficulty: String = "medium",
    val players: List<ParticipantModel> = mutableListOf(),
    val startTime: Long? = null,
    val endTime: Long? = null
) {
    fun toDocument(includeId: Boolean = true): Document {
        val jsonString = Json.encodeToString(this)
        val document = Document.parse(jsonString)

        if (includeId && id != null) {
            document["_id"] = id
        } else {
            document.remove("_id")
        }

        return document
    }

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
        }

        fun fromDocument(document: Document): ContestModel {
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
