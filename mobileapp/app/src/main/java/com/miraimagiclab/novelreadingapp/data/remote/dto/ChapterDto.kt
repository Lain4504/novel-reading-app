package com.miraimagiclab.novelreadingapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChapterDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("novelId")
    val novelId: String,
    
    @SerializedName("chapterTitle")
    val chapterTitle: String,
    
    @SerializedName("chapterNumber")
    val chapterNumber: Int,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("wordCount")
    val wordCount: Int,
    
    @SerializedName("viewCount")
    val viewCount: Int,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String
)


