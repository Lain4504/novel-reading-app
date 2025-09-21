package com.miraimagiclab.novelreadingapp.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import java.time.LocalDateTime

@Document(collection = "reviews")
data class Review(
    @Id
    val id: String? = null,

    @Indexed
    @Field(targetType = FieldType.OBJECT_ID)
    val userId: String,

    @Indexed
    @Field(targetType = FieldType.OBJECT_ID)
    val novelId: String,

    // Overall rating (average of all category ratings)
    val overallRating: Double,

    // Category ratings (1-5 stars each, all required)
    val writingQuality: Int,      // Chất lượng viết
    val stabilityOfUpdates: Int,  // Độ ổn định cập nhật
    val storyDevelopment: Int,    // Phát triển cốt truyện
    val characterDesign: Int,     // Thiết kế nhân vật
    val worldBackground: Int,     // Bối cảnh thế giới

    // Review content (required)
    val reviewText: String,

    // Derived metadata
    val wordCount: Int = reviewText.split("\\s+".toRegex()).size,

    // Reading progress when review was written
    val chaptersReadWhenReviewed: Int,
    val totalChaptersAtReview: Int,

    // Metadata
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    val version: Int = 1
)