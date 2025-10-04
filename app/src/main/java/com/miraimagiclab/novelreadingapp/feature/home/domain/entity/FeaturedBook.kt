package com.miraimagiclab.novelreadingapp.feature.home.domain.entity

data class FeaturedBook(
    val id: String,
    val title: String,
    val author: String,
    val coverImageUrl: String?,
    val description: String?,
    val genre: String?,
    val rating: Double?,
    val totalChapters: Int?,
    val isCompleted: Boolean = false,
    val featuredReason: String? = null
)
