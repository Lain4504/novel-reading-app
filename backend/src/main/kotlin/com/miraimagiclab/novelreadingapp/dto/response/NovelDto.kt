package com.miraimagiclab.novelreadingapp.dto.response

import com.miraimagiclab.novelreadingapp.model.CategoryEnum
import com.miraimagiclab.novelreadingapp.model.Novel
import com.miraimagiclab.novelreadingapp.model.NovelStatusEnum
import java.time.LocalDateTime

data class NovelDto(
    val id: String,
    val title: String,
    val description: String,
    val authorName: String,
    val coverImage: String?,
    val categories: Set<CategoryEnum>,
    val viewCount: Int,
    val followCount: Int,
    val commentCount: Int,
    val rating: Double,
    val ratingCount: Int,
    val wordCount: Int,
    val chapterCount: Int,
    val authorId: String?,
    val status: NovelStatusEnum,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isR18: Boolean
) {
    companion object {
        fun fromEntity(novel: Novel): NovelDto {
            return NovelDto(
                id = novel.id ?: "",
                title = novel.title,
                description = novel.description,
                authorName = novel.authorName,
                coverImage = novel.coverImage,
                categories = novel.categories,
                viewCount = novel.viewCount,
                followCount = novel.followCount,
                commentCount = novel.commentCount,
                rating = novel.rating,
                ratingCount = novel.ratingCount,
                wordCount = novel.wordCount,
                chapterCount = novel.chapterCount,
                authorId = novel.authorId,
                status = novel.status,
                createdAt = novel.createdAt,
                updatedAt = novel.updatedAt,
                isR18 = novel.isR18
            )
        }
    }
}
