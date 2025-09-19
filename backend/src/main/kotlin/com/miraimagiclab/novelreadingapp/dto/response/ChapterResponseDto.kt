package com.miraimagiclab.novelreadingapp.dto.response

data class ChapterResponseDto(
    val id: String,
    val novelId: String,
    val chapterTitle: String,
    val chapterNumber: Int,
    val content: String,
    val wordCount: Int,
    val viewCount: Int,
    val createdAt: String,
    val updatedAt: String
)
