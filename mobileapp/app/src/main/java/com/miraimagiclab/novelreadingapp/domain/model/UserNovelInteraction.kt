package com.miraimagiclab.novelreadingapp.domain.model

import java.time.LocalDateTime

data class UserNovelInteraction(
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


