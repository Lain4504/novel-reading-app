package com.miraimagiclab.novelreadingapp.feature.home.data.model

import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.FeaturedBook as DomainFeaturedBook

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
) {
    fun toDomain(): DomainFeaturedBook {
        return DomainFeaturedBook(
            id = id,
            title = title,
            author = author,
            coverImageUrl = coverImageUrl,
            description = description,
            genre = genre,
            rating = rating,
            totalChapters = totalChapters,
            isCompleted = isCompleted,
            featuredReason = featuredReason
        )
    }
}
