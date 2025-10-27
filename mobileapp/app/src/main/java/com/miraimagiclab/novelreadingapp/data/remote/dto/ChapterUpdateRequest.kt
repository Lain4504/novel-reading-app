package com.miraimagiclab.novelreadingapp.data.remote.dto

data class ChapterUpdateRequest(
    val chapterTitle: String? = null,
    val chapterNumber: Int? = null,
    val content: String? = null
)
