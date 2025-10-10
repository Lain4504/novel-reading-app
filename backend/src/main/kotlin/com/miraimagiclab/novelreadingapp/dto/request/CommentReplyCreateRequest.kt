package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.NotBlank

data class CommentReplyCreateRequest(
    @field:NotBlank(message = "Content cannot be blank")
    val content: String
)
