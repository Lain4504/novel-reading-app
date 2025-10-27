package com.miraimagiclab.novelreadingapp.data.remote.dto.request

data class ReviewCreateRequestDto(
    val userId: String,
    val novelId: String,
    val writingQuality: Int,
    val stabilityOfUpdates: Int,
    val storyDevelopment: Int,
    val characterDesign: Int,
    val worldBackground: Int,
    val reviewText: String,
    val chaptersReadWhenReviewed: Int,
    val totalChaptersAtReview: Int
) {
    val overallRating: Double
        get() = (writingQuality + stabilityOfUpdates + storyDevelopment + characterDesign + worldBackground) / 5.0
}
