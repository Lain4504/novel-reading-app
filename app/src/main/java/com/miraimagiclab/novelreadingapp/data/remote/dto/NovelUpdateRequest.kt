package com.miraimagiclab.novelreadingapp.data.remote.dto

data class NovelUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val authorName: String? = null,
    val authorId: String? = null,
    val categories: Set<String>? = null,
    val rating: Double? = null,
    val wordCount: Int? = null,
    val chapterCount: Int? = null,
    val status: String? = null,
    val isR18: Boolean? = null,
    val coverImage: String? = null
)
