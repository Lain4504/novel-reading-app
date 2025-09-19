package com.miraimagiclab.novelreadingapp.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


@Document(collection = "chapters")
data class Chapter(
    @Id
    val id: String? = null,

    val novelId: String,

    val chapterTitle: String,

    val chapterNumber: Number,

    val content: String,

    val wordCount: Int = 0,

    val viewCount: Int = 0,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    val updatedAt: LocalDateTime = LocalDateTime.now()
)