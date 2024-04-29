package com.sicsix.personalisedlearning.models

data class LoginResponse(
    val msg: String,
    val access_token: String,
    val user: UserDetails
)
