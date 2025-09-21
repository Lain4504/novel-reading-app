package com.miraimagiclab.novelreadingapp.dto.request

data class UserNovelInteractionUpdateRequest(
    val hasFollowing: Boolean? = null,
    val inWishlist: Boolean? = null,
    val notify: Boolean? = null,
    val currentChapterNumber: Int? = null,
    val currentChapterId: String? = null,
    val lastReadAt: String? = null, // ISO date string
    val totalChapterReads: Int? = null
)