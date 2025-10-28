package com.miraimagiclab.novelreadingapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("username")
    val username: String?,
    
    @SerializedName("avatarUrl")
    val avatarUrl: String?,
    
    @SerializedName("novelId")
    val novelId: String,
    
    @SerializedName("overallRating")
    val overallRating: Double,
    
    @SerializedName("writingQuality")
    val writingQuality: Int,
    
    @SerializedName("stabilityOfUpdates")
    val stabilityOfUpdates: Int,
    
    @SerializedName("storyDevelopment")
    val storyDevelopment: Int,
    
    @SerializedName("characterDesign")
    val characterDesign: Int,
    
    @SerializedName("worldBackground")
    val worldBackground: Int,
    
    @SerializedName("reviewText")
    val reviewText: String,
    
    @SerializedName("wordCount")
    val wordCount: Int,
    
    @SerializedName("chaptersReadWhenReviewed")
    val chaptersReadWhenReviewed: Int,
    
    @SerializedName("totalChaptersAtReview")
    val totalChaptersAtReview: Int,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String
)


