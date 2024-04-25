package com.sicsix.personalisedlearning.api

import com.sicsix.personalisedlearning.models.InterestsResponse
import com.sicsix.personalisedlearning.models.LoginResponse
import com.sicsix.personalisedlearning.models.MessageResponse
import com.sicsix.personalisedlearning.models.QuizUpdate
import com.sicsix.personalisedlearning.models.QuizzesResponse
import com.sicsix.personalisedlearning.models.UserInterests
import com.sicsix.personalisedlearning.models.UserLogin
import com.sicsix.personalisedlearning.models.UserRegistration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT


interface Api {
    @POST("/register")
    suspend fun registerUser(@Body user: UserRegistration): Response<LoginResponse>

    @POST("/login")
    suspend fun loginUser(@Body user: UserLogin): Response<LoginResponse>

    @GET("/interests")
    suspend fun getInterests(): Response<InterestsResponse>

    @PUT("/userinterests")
    suspend fun updateUserInterests(@Header("Authorization") token: String, @Body interests: UserInterests): Response<MessageResponse>

    @GET("/quizzes")
    suspend fun getQuizzes(@Header("Authorization") token: String): Response<QuizzesResponse>

    @PUT("/quizzes")
    suspend fun completeQuiz(@Header("Authorization") token: String, @Body quizUpdate: QuizUpdate): Response<MessageResponse>

}