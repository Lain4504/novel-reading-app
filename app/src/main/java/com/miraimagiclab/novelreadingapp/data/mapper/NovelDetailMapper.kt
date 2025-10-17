package com.miraimagiclab.novelreadingapp.data.mapper

import com.miraimagiclab.novelreadingapp.data.Book
import com.miraimagiclab.novelreadingapp.data.BookDetail
import com.miraimagiclab.novelreadingapp.data.BookType
import com.miraimagiclab.novelreadingapp.data.Chapter
import com.miraimagiclab.novelreadingapp.data.Comment
import com.miraimagiclab.novelreadingapp.data.Reply
import com.miraimagiclab.novelreadingapp.data.Review
import com.miraimagiclab.novelreadingapp.data.remote.dto.ChapterDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.CommentDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.NovelDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.ReviewDto
import com.miraimagiclab.novelreadingapp.domain.model.Novel

object NovelDetailMapper {

    fun mapNovelDtoToBook(novelDto: NovelDto): Book {
        return Book(
            id = novelDto.id,
            title = novelDto.title,
            author = novelDto.authorName,
            series = null, // Backend doesn't have series field
            type = mapStatusToBookType(novelDto.status),
            genres = novelDto.categories.toList(),
            score = novelDto.rating.toInt(),
            coverUrl = novelDto.coverImage ?: "",
            readTime = "${novelDto.wordCount} words",
            releaseDate = novelDto.createdAt,
            isCompleted = novelDto.status.equals("COMPLETED", ignoreCase = true)
        )
    }

    fun mapChapterDtoToChapter(chapterDto: ChapterDto): Chapter {
        return Chapter(
            id = chapterDto.id,
            title = chapterDto.chapterTitle,
            isUnlocked = true, // Assume all chapters are unlocked
            createdAt = chapterDto.createdAt,
            content = chapterDto.content
        )
    }

    fun mapCommentDtoToComment(commentDto: CommentDto, replies: List<CommentDto> = emptyList()): Comment {
        val replyList = replies.map { replyDto ->
            Reply(
                id = replyDto.id,
                username = replyDto.userId, // TODO: Replace with actual username when user service is available
                comment = replyDto.content,
                date = replyDto.createdAt,
                parentCommentId = replyDto.parentId ?: ""
            )
        }

        return Comment(
            id = commentDto.id,
            username = commentDto.userId, // TODO: Replace with actual username when user service is available
            comment = commentDto.content,
            date = commentDto.createdAt,
            replies = replyList
        )
    }

    fun mapReviewDtoToReview(reviewDto: ReviewDto): Review {
        return Review(
            id = reviewDto.id,
            username = reviewDto.userId, // TODO: Replace with actual username when user service is available
            rating = reviewDto.overallRating.toInt(),
            comment = reviewDto.reviewText,
            date = reviewDto.createdAt
        )
    }

    fun createBookDetail(
        novelDto: NovelDto,
        chapters: List<ChapterDto>,
        comments: List<CommentDto>,
        reviews: List<ReviewDto>,
        recommendations: List<NovelDto>
    ): BookDetail {
        val book = mapNovelDtoToBook(novelDto)
        
        val chapterList = chapters.map { mapChapterDtoToChapter(it) }
        
        // Group comments by parent-child relationship
        val topLevelComments = comments.filter { it.parentId == null }
        val replies = comments.filter { it.parentId != null }
        
        val commentList = topLevelComments.map { comment ->
            val commentReplies = replies.filter { it.parentId == comment.id }
            mapCommentDtoToComment(comment, commentReplies)
        }
        
        val reviewList = reviews.map { mapReviewDtoToReview(it) }
        val recommendationBooks = recommendations.map { mapNovelDtoToBook(it) }
        
        return BookDetail(
            book = book,
            summary = novelDto.description,
            chapters = chapterList,
            volumes = emptyList(), // Backend doesn't have volumes
            reviews = reviewList,
            comments = commentList,
            recommendations = recommendationBooks
        )
    }

    private fun mapStatusToBookType(status: String): BookType {
        return when (status.uppercase()) {
            "COMPLETED" -> BookType.NOVEL
            "ONGOING" -> BookType.NOVEL
            "HIATUS" -> BookType.NOVEL
            else -> BookType.NOVEL
        }
    }

}
