package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ReviewCreateRequest(
    @field:NotBlank(message = "User ID is required")
    val userId: String,

    @field:NotBlank(message = "Novel ID is required")
    val novelId: String,

    // Category ratings (1-5 stars each, all required)
    @field:NotNull(message = "Writing quality rating is required")
    @field:Min(1, message = "Rating must be at least 1")
    @field:Max(5, message = "Rating must be at most 5")
    val writingQuality: Int,

    @field:NotNull(message = "Stability of updates rating is required")
    @field:Min(1, message = "Rating must be at least 1")
    @field:Max(5, message = "Rating must be at most 5")
    val stabilityOfUpdates: Int,

    @field:NotNull(message = "Story development rating is required")
    @field:Min(1, message = "Rating must be at least 1")
    @field:Max(5, message = "Rating must be at most 5")
    val storyDevelopment: Int,

    @field:NotNull(message = "Character design rating is required")
    @field:Min(1, message = "Rating must be at least 1")
    @field:Max(5, message = "Rating must be at most 5")
    val characterDesign: Int,

    @field:NotNull(message = "World background rating is required")
    @field:Min(1, message = "Rating must be at least 1")
    @field:Max(5, message = "Rating must be at most 5")
    val worldBackground: Int,

    @field:NotBlank(message = "Review text is required")
    val reviewText: String,

    @field:NotNull(message = "Chapters read when reviewed is required")
    @field:Min(1, message = "Chapters read must be at least 1")
    val chaptersReadWhenReviewed: Int,

    @field:NotNull(message = "Total chapters at review time is required")
    @field:Min(1, message = "Total chapters must be at least 1")
    val totalChaptersAtReview: Int
) {
    val overallRating: Double
        get() = (writingQuality + stabilityOfUpdates + storyDevelopment + characterDesign + worldBackground) / 5.0
}