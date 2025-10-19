package com.miraimagiclab.novelreadingapp.data.remote.dto

data class ChapterCreateRequest(
    val chapterTitle: String,
    val chapterNumber: Int,
    val content: String
)
