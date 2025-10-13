package com.miraimagiclab.novelreadingapp.data.mapper

import com.miraimagiclab.novelreadingapp.data.local.entity.NovelEntity
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

    fun mapDomainToEntity(domain: Novel): NovelEntity {
        return NovelEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            authorName = domain.authorName,
            coverImage = domain.coverImage,
            categories = domain.categories,
            viewCount = domain.viewCount,
            followCount = domain.followCount,
            commentCount = domain.commentCount,
            rating = domain.rating,
            ratingCount = domain.ratingCount,
            wordCount = domain.wordCount,
            chapterCount = domain.chapterCount,
            authorId = domain.authorId,
            status = domain.status.name,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            isR18 = domain.isR18
        )
    }

    fun mapEntityToDomain(entity: NovelEntity): Novel {
        return Novel(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            authorName = entity.authorName,
            coverImage = entity.coverImage,
            categories = entity.categories,
            viewCount = entity.viewCount,
            followCount = entity.followCount,
            commentCount = entity.commentCount,
            rating = entity.rating,
            ratingCount = entity.ratingCount,
            wordCount = entity.wordCount,
            chapterCount = entity.chapterCount,
            authorId = entity.authorId,
            status = mapStatusStringToEnum(entity.status),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            isR18 = entity.isR18
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
