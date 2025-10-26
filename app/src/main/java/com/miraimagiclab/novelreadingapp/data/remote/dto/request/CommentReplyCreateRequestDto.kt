package com.miraimagiclab.novelreadingapp.data.remote.dto.request

data class CommentReplyCreateRequestDto(
    val content: String,
    val userId: String,
    val replyToUserName: String
)
