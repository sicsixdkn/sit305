package com.sicsix.talecraft.models.dtos

data class StoryRequest(
    val use_local_llm: Boolean,
    val story: String,
    val user_selection: String,
    val world: WorldInfo
)