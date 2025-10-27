package com.miraimagiclab.novelreadingapp.data.remote.dto

import com.miraimagiclab.novelreadingapp.domain.model.NovelStatus

data class NovelSearchRequest(
    val title: String? = null,
    val authorName: String? = null,
    val categories: Set<String>? = null,
    val status: String? = null,
    val minRating: Double? = null,
    val maxRating: Double? = null,
    val isR18: Boolean? = null,
    val sortBy: String = "updatedAt", // createdAt, updatedAt, viewCount, followCount, rating
    val sortDirection: String = "desc", // asc, desc
    val page: Int = 0,
    val size: Int = 20
)
