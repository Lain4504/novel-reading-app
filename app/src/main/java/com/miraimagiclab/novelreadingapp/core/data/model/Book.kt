package com.miraimagiclab.novelreadingapp.core.data.model

/**
 * Shared Book model for all features
 * Used across home, book details, search, etc.
 */
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
    val isCompleted: Boolean = false,
    val description: String? = null,
    val rating: Double? = null
)

enum class BookType {
    NOVEL, LIGHT_NOVEL, MANGA
}
