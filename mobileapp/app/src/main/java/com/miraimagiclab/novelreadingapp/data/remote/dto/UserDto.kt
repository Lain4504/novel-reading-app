package com.miraimagiclab.novelreadingapp.data.remote.dto

data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val roles: List<String>,
    val status: String,
    val avatarUrl: String? = null,
    val backgroundUrl: String? = null,
    val authorName: String? = null,
    val bio: String? = null,
    val displayName: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val lastDisplayNameChangedAt: String? = null
)