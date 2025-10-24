package com.miraimagiclab.novelreadingapp.data.mapper

import com.miraimagiclab.novelreadingapp.data.remote.dto.UserNovelInteractionDto
import com.miraimagiclab.novelreadingapp.domain.model.ReadingProgress
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object ReadingProgressMapper {
    
    fun mapDtoToDomain(dto: UserNovelInteractionDto): ReadingProgress {
        return ReadingProgress(
            userId = dto.userId,
            novelId = dto.novelId,
            currentChapterId = dto.currentChapterId,
            currentChapterNumber = dto.currentChapterNumber,
            lastReadAt = dto.lastReadAt?.let { parseDateTime(it) }
        )
    }
    
    private fun parseDateTime(dateString: String): Date? {
        return try {
            val localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        } catch (e: Exception) {
            null
        }
    }
}
