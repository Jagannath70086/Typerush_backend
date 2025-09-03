package com.typer.practice.domain.models

import com.typer.core.serialization.ObjectIdSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId

@Serializable
data class PracticeInfo(
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId? = null,
    val text: String,
    val time: Long,
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

        fun fromDocument(document: Document): PracticeInfo {
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