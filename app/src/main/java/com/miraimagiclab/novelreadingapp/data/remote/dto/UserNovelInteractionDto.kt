package com.miraimagiclab.novelreadingapp.data.remote.dto

import java.time.LocalDateTime

data class UserNovelInteractionDto(
    val id: String,
    val userId: String,
    val novelId: String,
    val hasFollowing: Boolean,
    val inWishlist: Boolean,
    val notify: Boolean,
    val currentChapterNumber: Int?,
    val currentChapterId: String?,
    val lastReadAt: LocalDateTime?,
    val totalChapterReads: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
