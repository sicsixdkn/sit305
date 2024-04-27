package com.sicsix.llama2chatbot.model

data class ChatRequest(
    val userMessage: String,
    val chatHistory: List<ChatEntry>
)
