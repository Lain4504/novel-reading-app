package com.miraimagiclab.novelreadingapp.dto.response

import com.miraimagiclab.novelreadingapp.enumeration.CommentEnum
import java.time.LocalDateTime

data class CommentResponseDto(
    val id: String,
    val content: String,
    val userId: String,
    val targetType: CommentEnum,
    val novelId: String?,
    val parentId: String?,
    val level: Int?,
    val replyToId: String?,
    val replyToUserName: String?,
    val likeCount: Int?,
    val replyCount: Int?,
    val deleted: Boolean,
    val message: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)