package com.typer.waiting.domain.models

import com.typer.auth.domain.models.UserModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId
import kotlin.collections.set

@Serializable
data class GameInfoModel(
    val id: String? = null,
    val text: String,
    val time: Long,
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

        fun fromDocument(document: Document): GameInfoModel {
            val mutableDoc = Document(document)

            return json.decodeFromString(mutableDoc.toJson())
        }
    }
}