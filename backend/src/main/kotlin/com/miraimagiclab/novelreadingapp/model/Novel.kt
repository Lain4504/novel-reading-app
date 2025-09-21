package com.miraimagiclab.novelreadingapp.model

import com.miraimagiclab.novelreadingapp.enumeration.CategoryEnum
import com.miraimagiclab.novelreadingapp.enumeration.NovelStatusEnum
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document(collection = "novels")
data class Novel(
    @Id
    val id: String? = null,

    @Indexed
    val title: String,

    val description: String,

    val authorName: String,

    val coverImage: String? = null,

    @Indexed
    val categories: Set<CategoryEnum> = emptySet(),

    val viewCount: Int = 0,

    val followCount: Int = 0,

    val commentCount: Int = 0,

    val rating: Double = 0.0,

    val ratingCount: Int = 0,

    val wordCount: Int = 0,

    val chapterCount: Int = 0,

    val authorId: String? = null,

    val status: NovelStatusEnum = NovelStatusEnum.DRAFT,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    val updatedAt: LocalDateTime = LocalDateTime.now(),

    val isR18: Boolean = false
)
