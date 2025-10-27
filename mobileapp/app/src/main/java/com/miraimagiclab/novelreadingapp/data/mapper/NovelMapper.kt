package com.miraimagiclab.novelreadingapp.data.mapper

import com.miraimagiclab.novelreadingapp.data.remote.dto.NovelDto
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.model.NovelStatus

object NovelMapper {

    fun mapDtoToDomain(dto: NovelDto): Novel {
        return Novel(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            authorName = dto.authorName,
            coverImage = dto.coverImage,
            categories = dto.categories.toList(),
            viewCount = dto.viewCount,
            followCount = dto.followCount,
            commentCount = dto.commentCount,
            rating = dto.rating,
            ratingCount = dto.ratingCount,
            wordCount = dto.wordCount,
            chapterCount = dto.chapterCount,
            authorId = dto.authorId,
            status = mapStatusStringToEnum(dto.status),
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            isR18 = dto.isR18
        )
    }

    private fun mapStatusStringToEnum(status: String): NovelStatus {
        return try {
            NovelStatus.valueOf(status.uppercase())
        } catch (e: IllegalArgumentException) {
            NovelStatus.OTHER
        }
    }
}
