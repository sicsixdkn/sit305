package com.sicsix.llama2chatbot.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

@Entity(
    tableName = "chatentries",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = CASCADE
        )
    ]
)
data class ChatEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val userMessage: String,
    val botMessage: String?,
    val timestamp: Long
)

// Custom serializer for ChatEntry objects to convert them to the format required by the server
class ChatEntrySerializer : JsonSerializer<ChatEntry> {
    override fun serialize(
        src: ChatEntry,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("User", src.userMessage)
        jsonObject.addProperty("Llama", src.botMessage)
        return jsonObject
    }
}
