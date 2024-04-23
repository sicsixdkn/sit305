package com.sicsix.itube.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "playlistEntries",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistEntry(
    @PrimaryKey(autoGenerate = true) val entryId: Int = 0,
    val userId: Int,
    val videoUrl: String,
    val videoId: String
)
