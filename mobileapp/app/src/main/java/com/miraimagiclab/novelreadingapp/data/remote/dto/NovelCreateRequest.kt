package com.miraimagiclab.novelreadingapp.data.remote.dto

data class NovelCreateRequest(
    val title: String,
    val description: String,
    val authorName: String,
    val authorId: String? = null,
    val categories: Set<String>,
    val status: String = "DRAFT",
    val isR18: Boolean = false,
    val coverImage: String? = null
)
