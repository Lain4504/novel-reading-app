package com.miraimagiclab.novelreadingapp.domain.model

data class Comment(
    val id: String,
    val content: String,
    val userId: String,
    val username: String? = null,
    val avatarUrl: String? = null,
    val targetType: String,
    val novelId: String? = null,
    val parentId: String? = null,
    val level: Int = 1,
    val replyToId: String? = null,
    val replyToUserName: String? = null,
    val likeCount: Int = 0,
    val replyCount: Int = 0,
    val deleted: Boolean = false,
    val message: String? = null,
    val createdAt: String,
    val updatedAt: String
)
