package com.miraimagiclab.novelreadingapp.feature.home.data.model

import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.PickBook as DomainPickBook

data class PickBook(
    val id: String,
    val title: String,
    val author: String,
    val coverImageUrl: String?,
    val description: String?,
    val genre: String?,
    val rating: Double?,
    val totalChapters: Int?,
    val isCompleted: Boolean = false,
    val pickReason: String? = null
) {
    fun toDomain(): DomainPickBook {
        return DomainPickBook(
            id = id,
            title = title,
            author = author,
            coverImageUrl = coverImageUrl,
            description = description,
            genre = genre,
            rating = rating,
            totalChapters = totalChapters,
            isCompleted = isCompleted,
            pickReason = pickReason
        )
    }
}
