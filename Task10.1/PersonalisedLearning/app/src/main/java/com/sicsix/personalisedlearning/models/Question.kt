package com.sicsix.personalisedlearning.models

data class Question(
    val question: String,
    val options: List<String>,
    val correct_answer: String
)
