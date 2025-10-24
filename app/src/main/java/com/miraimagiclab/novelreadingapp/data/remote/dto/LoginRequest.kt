package com.miraimagiclab.novelreadingapp.data.remote.dto

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
) {
    init {
        require(usernameOrEmail.trim().isNotBlank()) { "Username or email cannot be blank" }
        require(password.trim().isNotBlank()) { "Password cannot be blank" }
    }
}