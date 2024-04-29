package com.sicsix.personalisedlearning.models

data class UserDetails(
    val id: String,
    val username: String,
    val email: String,
    val phone_number: String,
    val plan: String
)