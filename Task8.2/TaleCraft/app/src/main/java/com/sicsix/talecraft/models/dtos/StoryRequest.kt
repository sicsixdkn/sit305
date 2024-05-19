package com.sicsix.talecraft.models.dtos

data class StoryRequest(
    val story: String,
    val user_selection: String,
    val world: WorldInfo
)