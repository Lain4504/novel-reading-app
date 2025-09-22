package com.miraimagiclab.novelreadingapp.dto.response

import com.miraimagiclab.novelreadingapp.model.Review
import java.time.LocalDateTime

data class ReviewDto(
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
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(review: Review): ReviewDto {
            return ReviewDto(
                id = review.id ?: "",
                userId = review.userId,
                novelId = review.novelId,
                overallRating = review.overallRating,
                writingQuality = review.writingQuality,
                stabilityOfUpdates = review.stabilityOfUpdates,
                storyDevelopment = review.storyDevelopment,
                characterDesign = review.characterDesign,
                worldBackground = review.worldBackground,
                reviewText = review.reviewText,
                wordCount = review.wordCount,
                chaptersReadWhenReviewed = review.chaptersReadWhenReviewed,
                totalChaptersAtReview = review.totalChaptersAtReview,
                createdAt = review.createdAt,
                updatedAt = review.updatedAt
            )
        }
    }
}