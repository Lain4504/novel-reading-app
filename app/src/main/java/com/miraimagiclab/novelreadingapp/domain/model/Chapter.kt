package com.miraimagiclab.novelreadingapp.domain.model

data class Chapter(
    val id: String,
    val novelId: String,
    val chapterTitle: String,
    val chapterNumber: Int,
    val content: String,
    val wordCount: Int = 0,
    val viewCount: Int = 0,
    val createdAt: String,
    val updatedAt: String
)
