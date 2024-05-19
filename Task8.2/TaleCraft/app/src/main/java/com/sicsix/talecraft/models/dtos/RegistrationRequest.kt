package com.sicsix.talecraft.models.dtos

data class RegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val phone_number: String
)