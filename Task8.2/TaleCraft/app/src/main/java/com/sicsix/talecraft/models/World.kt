package com.sicsix.talecraft.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "worlds")
data class World(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val genre: String,
    val subGenre: String,
    val premise: String,
    val lastAccessed: Long = System.currentTimeMillis()
)