package com.miraimagiclab.novelreadingapp.dto.request

import com.miraimagiclab.novelreadingapp.model.CategoryEnum
import com.miraimagiclab.novelreadingapp.model.NovelStatusEnum

data class NovelSearchRequest(
    val title: String? = null,
    val authorName: String? = null,
    val categories: Set<CategoryEnum>? = null,
    val status: NovelStatusEnum? = null,
    val minRating: Double? = null,
    val maxRating: Double? = null,
    val isR18: Boolean? = null,
    val sortBy: String = "createdAt", // createdAt, updatedAt, viewCount, followCount, rating
    val sortDirection: String = "desc", // asc, desc
    val page: Int = 0,
    val size: Int = 20
)
