package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import com.miraimagiclab.novelreadingapp.enumeration.CommentEnum

data class CommentRequest(
    @field:NotBlank val content: String,
    @field:NotBlank val userId: String,
    val targetType: CommentEnum,
    val novelId: String? = null,
    val parentId: String? = null,
    val level: Int? = 1,
    val replyToId: String? = null,
    val replyToUserName: String? = null
)