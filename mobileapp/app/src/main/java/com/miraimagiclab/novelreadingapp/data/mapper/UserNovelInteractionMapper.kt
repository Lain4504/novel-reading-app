package com.miraimagiclab.novelreadingapp.data.mapper

import com.miraimagiclab.novelreadingapp.data.remote.dto.UserNovelInteractionDto
import com.miraimagiclab.novelreadingapp.domain.model.UserNovelInteraction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object UserNovelInteractionMapper {

    fun mapDtoToDomain(dto: UserNovelInteractionDto): UserNovelInteraction {
        return UserNovelInteraction(
            id = dto.id,
            userId = dto.userId,
            novelId = dto.novelId,
            hasFollowing = dto.hasFollowing,
            inWishlist = dto.inWishlist,
            notify = dto.notify,
            currentChapterNumber = dto.currentChapterNumber,
            currentChapterId = dto.currentChapterId,
            lastReadAt = dto.lastReadAt?.let { parseDateTime(it) },
            totalChapterReads = dto.totalChapterReads,
            createdAt = parseDateTime(dto.createdAt),
            updatedAt = parseDateTime(dto.updatedAt)
        )
    }
    
    private fun parseDateTime(dateString: String): LocalDateTime {
        return try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            // Return current time if parsing fails
            LocalDateTime.now()
        }
    }
}


