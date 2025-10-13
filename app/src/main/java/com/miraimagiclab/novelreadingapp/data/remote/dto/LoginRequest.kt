package com.miraimagiclab.novelreadingapp.data.remote.dto

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)