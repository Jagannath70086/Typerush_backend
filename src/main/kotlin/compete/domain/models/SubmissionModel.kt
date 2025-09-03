package com.typer.compete.domain.models

import com.typer.core.serialization.ObjectIdSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId

@Serializable
data class SubmissionModel(
    val contestId: String,
    val userId: String,
    val wpm: Int,
    val accuracy: Double,
) {
    fun toDocument(): Document {
        val jsonString = Json.encodeToString(this)
        val document = Document.parse(jsonString)


        return document
    }

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
        }

        fun fromDocument(document: Document): SubmissionModel {
            val mutableDoc = Document(document)
//            document["_id"]?.let { objectId ->
//                if (objectId is ObjectId) {
//                    mutableDoc["_id"] = objectId.toHexString()
//                }
//            }

            return json.decodeFromString(mutableDoc.toJson())
        }
    }
}