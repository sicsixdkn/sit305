package com.sicsix.quizapp.model

data class Question(
    val questionText: String,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    val correctAnswerIndex: Int
)