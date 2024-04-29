package com.sicsix.lostandfound.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lostandfound")
data class LostAndFound(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isLostAdvert: Boolean,
    val name: String,
    val phone: String,
    val description: String,
    val date: Long,
    val location: String,
    val latitude: Double,
    val longitude: Double
)
