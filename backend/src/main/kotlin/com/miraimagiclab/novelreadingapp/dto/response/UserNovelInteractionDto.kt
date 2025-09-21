package com.miraimagiclab.novelreadingapp.dto.response

import com.miraimagiclab.novelreadingapp.model.UserNovelInteraction
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
) {
    companion object {
        fun fromEntity(interaction: UserNovelInteraction): UserNovelInteractionDto {
            return UserNovelInteractionDto(
                id = interaction.id ?: "",
                userId = interaction.userId,
                novelId = interaction.novelId,
                hasFollowing = interaction.hasFollowing,
                inWishlist = interaction.inWishlist,
                notify = interaction.notify,
                currentChapterNumber = interaction.currentChapterNumber,
                currentChapterId = interaction.currentChapterId,
                lastReadAt = interaction.lastReadAt,
                totalChapterReads = interaction.totalChapterReads,
                createdAt = interaction.createdAt,
                updatedAt = interaction.updatedAt
            )
        }
    }
}