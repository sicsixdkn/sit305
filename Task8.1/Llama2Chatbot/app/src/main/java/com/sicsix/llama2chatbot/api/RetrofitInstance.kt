package com.sicsix.llama2chatbot.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sicsix.llama2chatbot.model.ChatEntry
import com.sicsix.llama2chatbot.model.ChatEntrySerializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // Points to the local server
    private const val BASE_URL = "http://10.0.2.2:5000"

    // Custom Gson instance to handle ChatEntry serialization
    private val gson: Gson = GsonBuilder().registerTypeAdapter(ChatEntry::class.java, ChatEntrySerializer()).create()

    // Custom OkHttpClient instance to handle long read timeouts due to the slow response time of the bot
    private val client = OkHttpClient.Builder()
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    // Create an instance of the API interface
    val api: Api by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(Api::class.java)
    }
}