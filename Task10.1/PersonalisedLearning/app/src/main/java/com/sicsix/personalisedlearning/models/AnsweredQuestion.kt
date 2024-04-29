package com.sicsix.personalisedlearning.models

data class AnsweredQuestion(
    val topic: String,
    val question: String,
    val options: List<String>,
    val correct_answer: String,
    val selected_answer: String,
    val reasoning: String? = null
)