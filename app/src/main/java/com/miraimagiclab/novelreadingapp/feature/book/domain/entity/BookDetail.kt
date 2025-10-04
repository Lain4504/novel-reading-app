package com.miraimagiclab.novelreadingapp.feature.book.domain.entity

data class BookDetail(
    val book: Book,
    val summary: String,
    val chapters: List<Chapter>,
    val volumes: List<Volume>,
    val reviews: List<Review>,
    val recommendations: List<Book>
)

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val series: String? = null,
    val type: BookType,
    val genres: List<String>,
    val score: Int,
    val coverUrl: String,
    val readTime: String,
    val releaseDate: String,
    val isCompleted: Boolean = false
)

data class Chapter(
    val id: String,
    val title: String,
    val isUnlocked: Boolean = true,
    val thumbnailUrl: String? = null
)

data class Volume(
    val id: String,
    val title: String,
    val releaseDate: String,
    val coverUrl: String
)

data class Review(
    val id: String,
    val username: String,
    val rating: Int,
    val comment: String,
    val date: String
)

enum class BookType {
    NOVEL, LIGHT_NOVEL, MANGA
}
