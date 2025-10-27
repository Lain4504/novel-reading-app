package com.miraimagiclab.novelreadingapp.data.remote.dto

data class UserUpdateRequest(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val roles: Set<String>? = null,
    val status: String? = null,
    val avatarUrl: String? = null,
    val backgroundUrl: String? = null,
    val authorName: String? = null,
    val bio: String? = null,
    val displayName: String? = null
)
