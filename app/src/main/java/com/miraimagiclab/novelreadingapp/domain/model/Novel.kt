package com.miraimagiclab.novelreadingapp.domain.model

data class Novel(
    val id: String,
    val title: String,
    val description: String,
    val authorName: String,
    val coverImage: String? = null,
    val categories: List<String>,
    val viewCount: Int,
    val followCount: Int,
    val commentCount: Int,
    val rating: Double,
    val ratingCount: Int,
    val wordCount: Int,
    val chapterCount: Int,
    val authorId: String? = null,
    val status: NovelStatus,
    val createdAt: String,
    val updatedAt: String,
    val isR18: Boolean
)

enum class NovelStatus {
    DRAFT,
    PUBLISHED,
    ONGOING,
    COMPLETED,
    HIATUS,
    CANCELLED,
    OTHER
}
