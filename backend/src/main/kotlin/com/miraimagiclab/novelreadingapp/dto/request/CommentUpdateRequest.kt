package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.NotBlank

data class CommentUpdateRequest(
    @field:NotBlank(message = "Content cannot be blank")
    val content: String
)
