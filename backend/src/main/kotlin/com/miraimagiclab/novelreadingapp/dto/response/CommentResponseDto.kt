// CommentResponseDto.kt
package com.miraimagiclab.novelreadingapp.dto.response

import com.miraimagiclab.novelreadingapp.model.Comment
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
) {
    companion object {
        fun fromEntity(comment: Comment): CommentResponseDto {
            return CommentResponseDto(
                id = comment.id ?: "",
                content = comment.content,
                userId = comment.userId ?: "",
                targetType = comment.targetType.name,
                novelId = comment.novelId,
                parentId = comment.parentId,
                level = comment.level ?: 1,
                replyToId = comment.replyToId,
                replyToUserName = comment.replyToUserName,
                likeCount = comment.likeCount ?: 0,
                replyCount = comment.replyCount ?: 0,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        }
    }
}
