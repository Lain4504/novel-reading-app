package com.miraimagiclab.novelreadingapp.data.remote.dto.request

data class CommentCreateRequestDto(
    val content: String,
    val targetType: String = "NOVEL",
    val userId: String,
    val novelId: String? = null
)
