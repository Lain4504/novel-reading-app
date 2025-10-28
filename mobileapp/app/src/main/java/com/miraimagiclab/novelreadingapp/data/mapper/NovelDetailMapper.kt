package com.miraimagiclab.novelreadingapp.data.mapper

import com.miraimagiclab.novelreadingapp.data.remote.dto.ChapterDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.CommentDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.NovelDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.ReviewDto
import com.miraimagiclab.novelreadingapp.domain.model.*

object NovelDetailMapper {

    fun mapNovelDtoToNovel(novelDto: NovelDto): Novel {
        return Novel(
            id = novelDto.id,
            title = novelDto.title,
            description = novelDto.description,
            authorName = novelDto.authorName,
            coverImage = novelDto.coverImage,
            categories = novelDto.categories.map { it.toString() },
            viewCount = novelDto.viewCount,
            followCount = novelDto.followCount,
            commentCount = novelDto.commentCount,
            rating = novelDto.rating,
            ratingCount = novelDto.ratingCount,
            wordCount = novelDto.wordCount,
            chapterCount = novelDto.chapterCount,
            authorId = novelDto.authorId,
            status = mapStatusToNovelStatus(novelDto.status.toString()),
            createdAt = novelDto.createdAt.toString(),
            updatedAt = novelDto.updatedAt.toString(),
            isR18 = novelDto.isR18
        )
    }

    fun mapChapterDtoToChapter(chapterDto: ChapterDto): Chapter {
        return Chapter(
            id = chapterDto.id,
            novelId = chapterDto.novelId,
            chapterTitle = chapterDto.chapterTitle,
            chapterNumber = chapterDto.chapterNumber,
            content = chapterDto.content,
            wordCount = chapterDto.wordCount,
            viewCount = chapterDto.viewCount,
            createdAt = chapterDto.createdAt,
            updatedAt = chapterDto.updatedAt
        )
    }

    fun mapCommentDtoToComment(commentDto: CommentDto): Comment {
        return Comment(
            id = commentDto.id,
            content = commentDto.content,
            userId = commentDto.userId,
            username = commentDto.username,
            avatarUrl = commentDto.avatarUrl,
            targetType = commentDto.targetType,
            novelId = commentDto.novelId,
            parentId = commentDto.parentId,
            level = commentDto.level,
            replyToId = commentDto.replyToId,
            replyToUserName = commentDto.replyToUserName,
            likeCount = commentDto.likeCount,
            replyCount = commentDto.replyCount,
            deleted = false, // Default value
            message = null, // Default value
            createdAt = commentDto.createdAt.toString(),
            updatedAt = commentDto.updatedAt.toString()
        )
    }

    fun mapReviewDtoToReview(reviewDto: ReviewDto): Review {
        return Review(
            id = reviewDto.id,
            userId = reviewDto.userId,
            username = reviewDto.username,
            avatarUrl = reviewDto.avatarUrl,
            novelId = reviewDto.novelId,
            overallRating = reviewDto.overallRating,
            writingQuality = reviewDto.writingQuality,
            stabilityOfUpdates = reviewDto.stabilityOfUpdates,
            storyDevelopment = reviewDto.storyDevelopment,
            characterDesign = reviewDto.characterDesign,
            worldBackground = reviewDto.worldBackground,
            reviewText = reviewDto.reviewText,
            wordCount = reviewDto.wordCount,
            chaptersReadWhenReviewed = reviewDto.chaptersReadWhenReviewed,
            totalChaptersAtReview = reviewDto.totalChaptersAtReview,
            createdAt = reviewDto.createdAt.toString(),
            updatedAt = reviewDto.updatedAt.toString()
        )
    }

    fun createNovelDetail(
        novelDto: NovelDto,
        chapters: List<ChapterDto>,
        comments: List<CommentDto>,
        reviews: List<ReviewDto>,
        recommendations: List<NovelDto>
    ): NovelDetail {
        val novel = mapNovelDtoToNovel(novelDto)
        
        val chapterList = chapters.map { mapChapterDtoToChapter(it) }
        val commentList = comments.map { mapCommentDtoToComment(it) }
        val reviewList = reviews.map { mapReviewDtoToReview(it) }
        val recommendationNovels = recommendations.map { mapNovelDtoToNovel(it) }
        
        return NovelDetail(
            novel = novel,
            chapters = chapterList,
            reviews = reviewList,
            comments = commentList,
            recommendations = recommendationNovels
        )
    }

    private fun mapStatusToNovelStatus(status: String): NovelStatus {
        return when (status.uppercase()) {
            "COMPLETED" -> NovelStatus.COMPLETED
            "ONGOING" -> NovelStatus.ONGOING
            "HIATUS" -> NovelStatus.HIATUS
            "DRAFT" -> NovelStatus.DRAFT
            "PUBLISHED" -> NovelStatus.PUBLISHED
            "CANCELLED" -> NovelStatus.CANCELLED
            else -> NovelStatus.OTHER
        }
    }

}
