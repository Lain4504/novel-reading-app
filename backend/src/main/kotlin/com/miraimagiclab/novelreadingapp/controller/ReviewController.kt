package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.dto.request.ReviewCreateRequest
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.dto.response.ReviewDto
import com.miraimagiclab.novelreadingapp.service.ReviewService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000", "http://127.0.0.1:8080"])
@Tag(name = "Review Management", description = "APIs for managing novel reviews")
class ReviewController(
    private val reviewService: ReviewService
) {

    @PostMapping
    fun createReview(@Valid @RequestBody request: ReviewCreateRequest): ResponseEntity<ApiResponse<ReviewDto>> {
        val review = reviewService.createReview(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(review, "Review created successfully"))
    }

    @GetMapping("/{id}")
    fun getReviewById(@PathVariable id: String): ResponseEntity<ApiResponse<ReviewDto>> {
        val review = reviewService.getReviewById(id)
        return ResponseEntity.ok(ApiResponse.success(review, "Review retrieved successfully"))
    }

    @GetMapping("/users/{userId}")
    fun getReviewsByUser(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PageResponse<ReviewDto>>> {
        val reviews = reviewService.getReviewsByUser(userId, page, size)
        return ResponseEntity.ok(ApiResponse.success(reviews, "User reviews retrieved successfully"))
    }

    @GetMapping("/novels/{novelId}")
    fun getReviewsByNovel(
        @PathVariable novelId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PageResponse<ReviewDto>>> {
        val reviews = reviewService.getReviewsByNovel(novelId, page, size)
        return ResponseEntity.ok(ApiResponse.success(reviews, "Novel reviews retrieved successfully"))
    }

    @GetMapping("/users/{userId}/novels/{novelId}")
    fun getReviewByUserAndNovel(
        @PathVariable userId: String,
        @PathVariable novelId: String
    ): ResponseEntity<ApiResponse<ReviewDto?>> {
        val review = reviewService.getReviewByUserAndNovel(userId, novelId)
        return ResponseEntity.ok(ApiResponse.success(review, "Review retrieved successfully"))
    }

    @GetMapping("/novels/{novelId}/top")
    fun getTopReviewsByNovel(
        @PathVariable novelId: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ApiResponse<List<ReviewDto>>> {
        val reviews = reviewService.getTopReviewsByNovel(novelId, limit)
        return ResponseEntity.ok(ApiResponse.success(reviews, "Top reviews retrieved successfully"))
    }

    @GetMapping("/novels/{novelId}/average-rating")
    fun getAverageRatingByNovel(@PathVariable novelId: String): ResponseEntity<ApiResponse<Double?>> {
        val averageRating = reviewService.getAverageRatingByNovel(novelId)
        return ResponseEntity.ok(ApiResponse.success(averageRating, "Average rating retrieved successfully"))
    }

    @GetMapping("/novels/{novelId}/count")
    fun getReviewCountByNovel(@PathVariable novelId: String): ResponseEntity<ApiResponse<Long>> {
        val count = reviewService.getReviewCountByNovel(novelId)
        return ResponseEntity.ok(ApiResponse.success(count, "Review count retrieved successfully"))
    }

    @GetMapping("/users/{userId}/count")
    fun getReviewCountByUser(@PathVariable userId: String): ResponseEntity<ApiResponse<Long>> {
        val count = reviewService.getReviewCountByUser(userId)
        return ResponseEntity.ok(ApiResponse.success(count, "Review count retrieved successfully"))
    }

    @DeleteMapping("/{id}")
    fun deleteReview(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        reviewService.deleteReview(id)
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"))
    }

    @DeleteMapping("/users/{userId}/novels/{novelId}")
    fun deleteReviewByUserAndNovel(
        @PathVariable userId: String,
        @PathVariable novelId: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        reviewService.deleteReviewByUserAndNovel(userId, novelId)
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"))
    }
}