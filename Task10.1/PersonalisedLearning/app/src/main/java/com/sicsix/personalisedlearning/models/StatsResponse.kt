package com.sicsix.personalisedlearning.models

data class StatsResponse (
    val total_questions: Int,
    val correct_answers: Int,
    val quizzes_ready: Int
)