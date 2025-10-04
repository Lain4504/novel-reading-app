package com.miraimagiclab.novelreadingapp.data

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

data class BookDetail(
    val book: Book,
    val summary: String,
    val chapters: List<Chapter>,
    val volumes: List<Volume>,
    val reviews: List<Review>,
    val comments: List<Comment>,
    val recommendations: List<Book>
)

data class Chapter(
    val id: String,
    val title: String,
    val isUnlocked: Boolean = true,
    val createdAt: String
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

data class Comment(
    val id: String,
    val username: String,
    val comment: String,
    val date: String,
    val replies: List<Reply> = emptyList()
)

data class Reply(
    val id: String,
    val username: String,
    val comment: String,
    val date: String,
    val parentCommentId: String
)

enum class BookType {
    NOVEL, LIGHT_NOVEL, MANGA
}

data class UserStats(
    val bookPoints: Int,
    val readBooks: Int
)
