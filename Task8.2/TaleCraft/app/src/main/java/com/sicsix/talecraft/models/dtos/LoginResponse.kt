package com.sicsix.talecraft.models.dtos

import com.sicsix.talecraft.models.UserDetails

data class LoginResponse(
    val msg: String,
    val access_token: String,
    val user: UserDetails
)
