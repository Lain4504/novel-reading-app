package com.miraimagiclab.novelreadingapp.data.mapper

import com.miraimagiclab.novelreadingapp.data.remote.dto.UserNovelInteractionDto
import com.miraimagiclab.novelreadingapp.domain.model.ReadingProgress
import java.time.ZoneId
import java.util.Date

object ReadingProgressMapper {
    
    fun mapDtoToDomain(dto: UserNovelInteractionDto): ReadingProgress {
        return ReadingProgress(
            userId = dto.userId,
            novelId = dto.novelId,
            currentChapterId = dto.currentChapterId,
            currentChapterNumber = dto.currentChapterNumber,
            lastReadAt = dto.lastReadAt?.let { 
                Date.from(it.atZone(ZoneId.systemDefault()).toInstant()) 
            }
        )
    }
}
