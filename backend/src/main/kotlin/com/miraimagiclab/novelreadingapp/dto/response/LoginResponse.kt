package com.miraimagiclab.novelreadingapp.dto.response

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: UserDto
)