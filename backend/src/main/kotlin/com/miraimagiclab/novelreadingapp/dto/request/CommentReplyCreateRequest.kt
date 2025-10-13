// CommentReplyCreateRequest.kt
package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.NotBlank

data class CommentReplyCreateRequest(
    @field:NotBlank(message = "Content cannot be blank")
    val content: String,

    @field:NotBlank(message = "User ID is required")
    val userId: String,

    @field:NotBlank(message = "Reply target username is required")
    val replyToUserName: String
)
