package com.miraimagiclab.novelreadingapp.data.mapper

import com.miraimagiclab.novelreadingapp.data.remote.dto.ChapterDto
import com.miraimagiclab.novelreadingapp.domain.model.Chapter

object ChapterMapper {

    fun mapDtoToDomain(dto: ChapterDto): Chapter {
        return Chapter(
            id = dto.id,
            novelId = dto.novelId,
            chapterTitle = dto.chapterTitle,
            chapterNumber = dto.chapterNumber,
            content = dto.content,
            wordCount = dto.wordCount,
            viewCount = dto.viewCount,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }
}
