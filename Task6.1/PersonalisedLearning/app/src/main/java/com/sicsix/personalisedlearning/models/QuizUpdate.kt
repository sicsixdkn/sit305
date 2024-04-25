package com.sicsix.personalisedlearning.models

data class QuizUpdate(
    val quiz_id: String,
    val selected_answers: List<String>
)
