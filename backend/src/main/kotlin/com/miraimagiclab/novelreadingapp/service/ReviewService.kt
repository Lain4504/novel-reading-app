package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.dto.request.ReviewCreateRequest
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.dto.response.ReviewDto
import com.miraimagiclab.novelreadingapp.exception.NovelNotFoundException
import com.miraimagiclab.novelreadingapp.exception.UserNotFoundException
import com.miraimagiclab.novelreadingapp.model.Review
import com.miraimagiclab.novelreadingapp.repository.NovelRepository
import com.miraimagiclab.novelreadingapp.repository.ReviewRepository
import com.miraimagiclab.novelreadingapp.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val novelRepository: NovelRepository
) {

    fun createReview(request: ReviewCreateRequest): ReviewDto {
        // Validate user exists
        if (!userRepository.existsById(request.userId)) {
            throw UserNotFoundException("User with ID '${request.userId}' not found")
        }

        // Validate novel exists
        if (!novelRepository.existsById(request.novelId)) {
            throw NovelNotFoundException("Novel with ID '${request.novelId}' not found")
        }

        // Check if user already reviewed this novel
        if (reviewRepository.existsByUserIdAndNovelId(request.userId, request.novelId)) {
            throw IllegalArgumentException("User has already reviewed this novel")
        }

        val review = Review(
            userId = request.userId,
            novelId = request.novelId,
            overallRating = request.overallRating,
            writingQuality = request.writingQuality,
            stabilityOfUpdates = request.stabilityOfUpdates,
            storyDevelopment = request.storyDevelopment,
            characterDesign = request.characterDesign,
            worldBackground = request.worldBackground,
            reviewText = request.reviewText,
            wordCount = request.reviewText.split("\\s+".toRegex()).size,
            chaptersReadWhenReviewed = request.chaptersReadWhenReviewed,
            totalChaptersAtReview = request.totalChaptersAtReview,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedReview = reviewRepository.save(review)
        return ReviewDto.fromEntity(savedReview)
    }

    @Transactional(readOnly = true)
    fun getReviewById(id: String): ReviewDto {
        val review = reviewRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Review with ID '$id' not found") }
        return ReviewDto.fromEntity(review)
    }

    @Transactional(readOnly = true)
    fun getReviewsByUser(userId: String, page: Int = 0, size: Int = 20): PageResponse<ReviewDto> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val reviews = reviewRepository.findByUserId(userId, pageable)

        val reviewDtos = reviews.content.map { ReviewDto.fromEntity(it) }
        return PageResponse(
            content = reviewDtos,
            page = reviews.number,
            size = reviews.size,
            totalElements = reviews.totalElements,
            totalPages = reviews.totalPages,
            first = reviews.isFirst,
            last = reviews.isLast,
            numberOfElements = reviews.numberOfElements
        )
    }

    @Transactional(readOnly = true)
    fun getReviewsByNovel(novelId: String, page: Int = 0, size: Int = 20): PageResponse<ReviewDto> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val reviews = reviewRepository.findByNovelId(novelId, pageable)

        val reviewDtos = reviews.content.map { ReviewDto.fromEntity(it) }
        return PageResponse(
            content = reviewDtos,
            page = reviews.number,
            size = reviews.size,
            totalElements = reviews.totalElements,
            totalPages = reviews.totalPages,
            first = reviews.isFirst,
            last = reviews.isLast,
            numberOfElements = reviews.numberOfElements
        )
    }

    @Transactional(readOnly = true)
    fun getReviewByUserAndNovel(userId: String, novelId: String): ReviewDto? {
        val review = reviewRepository.findByUserIdAndNovelId(userId, novelId)
        return review.map { ReviewDto.fromEntity(it) }.orElse(null)
    }

    @Transactional(readOnly = true)
    fun getTopReviewsByNovel(novelId: String, limit: Int = 10): List<ReviewDto> {
        return reviewRepository.findTop10ByNovelIdOrderByCreatedAtDesc(novelId)
            .take(limit)
            .map { ReviewDto.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getAverageRatingByNovel(novelId: String): Double? {
        val reviews = reviewRepository.findByNovelId(novelId)
        return if (reviews.isNotEmpty()) {
            reviews.map { it.overallRating }.average()
        } else {
            null
        }
    }

    @Transactional(readOnly = true)
    fun getReviewCountByNovel(novelId: String): Long {
        return reviewRepository.countByNovelId(novelId)
    }

    @Transactional(readOnly = true)
    fun getReviewCountByUser(userId: String): Long {
        return reviewRepository.countByUserId(userId)
    }

    fun deleteReview(id: String) {
        if (!reviewRepository.existsById(id)) {
            throw IllegalArgumentException("Review with ID '$id' not found")
        }
        reviewRepository.deleteById(id)
    }

    fun deleteReviewByUserAndNovel(userId: String, novelId: String) {
        val review = reviewRepository.findByUserIdAndNovelId(userId, novelId)
        if (review.isPresent) {
            reviewRepository.delete(review.get())
        }
    }
}