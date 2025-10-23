package com.miraimagiclab.novelreadingapp.data.mapper

import com.miraimagiclab.novelreadingapp.data.remote.dto.UserNovelInteractionDto
import com.miraimagiclab.novelreadingapp.domain.model.UserNovelInteraction

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
            lastReadAt = dto.lastReadAt,
            totalChapterReads = dto.totalChapterReads,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }
}


