package com.miraimagiclab.novelreadingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.miraimagiclab.novelreadingapp.data.local.converter.StringListConverter

@Entity(tableName = "novels")
@TypeConverters(StringListConverter::class)
data class NovelEntity(
    @PrimaryKey
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
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val isR18: Boolean,
    val lastFetched: Long = System.currentTimeMillis()
)
