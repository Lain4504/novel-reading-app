package com.miraimagiclab.novelreadingapp.data.remote.dto

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: UserDto
)