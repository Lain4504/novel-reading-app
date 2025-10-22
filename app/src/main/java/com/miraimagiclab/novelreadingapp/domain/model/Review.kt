package com.miraimagiclab.novelreadingapp.domain.model

data class Review(
    val id: String,
    val userId: String,
    val novelId: String,
    val overallRating: Double,
    val writingQuality: Int,
    val stabilityOfUpdates: Int,
    val storyDevelopment: Int,
    val characterDesign: Int,
    val worldBackground: Int,
    val reviewText: String,
    val wordCount: Int,
    val chaptersReadWhenReviewed: Int,
    val totalChaptersAtReview: Int,
    val createdAt: String,
    val updatedAt: String
)
