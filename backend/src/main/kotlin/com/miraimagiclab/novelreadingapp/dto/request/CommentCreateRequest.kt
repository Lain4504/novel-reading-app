package com.miraimagiclab.novelreadingapp.dto.request

import com.miraimagiclab.novelreadingapp.enumeration.CommentEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CommentCreateRequest(
    @field:NotBlank(message = "Content cannot be blank")
    val content: String,

    @field:NotNull(message = "Target type is required")
    val targetType: CommentEnum,

    // Nếu targetType = NOVEL → dùng novelId
    val novelId: String? = null
)
