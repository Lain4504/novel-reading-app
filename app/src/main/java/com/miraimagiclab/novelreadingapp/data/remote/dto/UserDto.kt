package com.miraimagiclab.novelreadingapp.data.remote.dto

data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val roles: List<String>,
    val status: String,
    val displayName: String? = null,
    val createdAt: String,
    val updatedAt: String
)