package com.sicsix.personalisedlearning.models

data class Quiz(
    val user_id: String,
    val quiz_id: String,
    val topic: String,
    val questions: List<Question>,
    val selected_answers: List<String>,
    val complete: Boolean,
    val score: Int,
)
