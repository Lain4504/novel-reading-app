package com.miraimagiclab.novelreadingapp.data.remote.dto

data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)