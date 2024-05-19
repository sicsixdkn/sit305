package com.sicsix.talecraft.api

import com.sicsix.talecraft.models.dtos.LoginRequest
import com.sicsix.talecraft.models.dtos.LoginResponse
import com.sicsix.talecraft.models.dtos.RegistrationRequest
import com.sicsix.talecraft.models.dtos.StoryRequest
import com.sicsix.talecraft.models.dtos.StoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface Api {
    // This is the endpoint for registering a user
    @POST("/register")
    suspend fun registerUser(@Body user: RegistrationRequest): Response<LoginResponse>

    // This is the endpoint for logging in a user
    @POST("/login")
    suspend fun loginUser(@Body user: LoginRequest): Response<LoginResponse>

    // This is the endpoint for generating story entries
    @POST("/story")
    suspend fun generateStoryEntry(@Header("Authorization") token: String, @Body storyRequest: StoryRequest): Response<StoryResponse>
}