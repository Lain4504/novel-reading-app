package com.miraimagiclab.novelreadingapp.data.remote.dto

data class UserNovelInteractionDto(
    val id: String,
    val userId: String,
    val novelId: String,
    val hasFollowing: Boolean,
    val inWishlist: Boolean,
    val notify: Boolean,
    val currentChapterNumber: Int?,
    val currentChapterId: String?,
    val lastReadAt: String?,
    val totalChapterReads: Int,
    val createdAt: String,
    val updatedAt: String
)
