package com.sicsix.talecraft.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "story_entries",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Story::class,
            parentColumns = ["id"],
            childColumns = ["storyId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class StoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val storyId: Int,
    val content: String,
    val isUserSelection: Boolean,
    val options: List<String>,
    val timestamp: Long = System.currentTimeMillis(),
)
