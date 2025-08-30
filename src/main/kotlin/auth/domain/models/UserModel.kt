package com.typer.auth.domain.models

import com.typer.core.serialization.ObjectIdSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId

@Serializable
data class UserModel(
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId? = null,
    val firebaseId: String? = null,
    val name: String = "Anonymous",
    val email: String = "abc@example.com",
    val photoUrl: String = "",
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

        fun fromDocument(document: Document): UserModel {
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

fun UserModel.toUserResponseModel(): UserResponseModel {
    return UserResponseModel(
        id = id?.toHexString(),
        name = name,
        email = email,
        photoUrl = photoUrl,
    )
}
