package com.sicsix.llama2chatbot.api


import com.sicsix.llama2chatbot.model.ChatRequest
import com.sicsix.llama2chatbot.model.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface Api {
    @POST("/chat")
    suspend fun postChat(@Body user: ChatRequest): Response<MessageResponse>
}