package com.miraimagiclab.novelreadingapp.dto.response

import java.time.LocalDateTime

data class CommentResponseDto(
    val id: String,
    val content: String,
    val userId: String,
    val targetType: String,
    val novelId: String?,
    val parentId: String?,
    val level: Int,
    val replyToId: String?,
    val replyToUserName: String?,
    val likeCount: Int,
    val replyCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
