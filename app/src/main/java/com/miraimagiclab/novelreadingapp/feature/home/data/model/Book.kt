package com.miraimagiclab.novelreadingapp.feature.home.data.model

import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.Book as DomainBook

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverImageUrl: String?,
    val description: String?,
    val genre: String?,
    val rating: Double?,
    val totalChapters: Int?,
    val isCompleted: Boolean = false
) {
    fun toDomain(): DomainBook {
        return DomainBook(
            id = id,
            title = title,
            author = author,
            coverImageUrl = coverImageUrl,
            description = description,
            genre = genre,
            rating = rating,
            totalChapters = totalChapters,
            isCompleted = isCompleted
        )
    }
}
