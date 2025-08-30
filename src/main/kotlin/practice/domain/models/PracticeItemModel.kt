package com.typer.practice.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId
import kotlin.collections.set
import com.typer.core.serialization.ObjectIdSerializer

@Serializable
data class PracticeItemModel(
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId? = null,
    val title: String,
    val tags: List<String>,
    val time: Int
) {
    fun toDocument(): Document {
        val jsonString = Json.encodeToString(this)
        val document = Document.parse(jsonString)

        if (id != null) {
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

        fun fromDocument(document: Document): PracticeItemModel {
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