package com.miraimagiclab.novelreadingapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NovelDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("authorName")
    val authorName: String,
    
    @SerializedName("coverImage")
    val coverImage: String? = null,
    
    @SerializedName("categories")
    val categories: Set<String>,
    
    @SerializedName("viewCount")
    val viewCount: Int,
    
    @SerializedName("followCount")
    val followCount: Int,
    
    @SerializedName("commentCount")
    val commentCount: Int,
    
    @SerializedName("rating")
    val rating: Double,
    
    @SerializedName("ratingCount")
    val ratingCount: Int,
    
    @SerializedName("wordCount")
    val wordCount: Int,
    
    @SerializedName("chapterCount")
    val chapterCount: Int,
    
    @SerializedName("authorId")
    val authorId: String? = null,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String,
    
    @SerializedName("isR18")
    val isR18: Boolean
)
