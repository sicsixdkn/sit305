package com.sicsix.talecraft.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "stories",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = World::class,
            parentColumns = ["id"],
            childColumns = ["worldId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class Story(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val worldId: Int,
    val title: String,
    val worldTitle: String,
    val wordCount: Int,
    val lastAccessed: Long = System.currentTimeMillis()
)