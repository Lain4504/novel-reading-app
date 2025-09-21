package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.dto.request.UserNovelInteractionUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.UserNovelInteractionDto
import com.miraimagiclab.novelreadingapp.service.UserNovelInteractionService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/interactions")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000", "http://127.0.0.1:8080"])
@Tag(name = "User Novel Interactions", description = "APIs for managing user interactions with novels")
class UserNovelInteractionController(
    private val userNovelInteractionService: UserNovelInteractionService
) {

    @GetMapping("/users/{userId}/novels/{novelId}")
    fun getUserNovelInteraction(
        @PathVariable userId: String,
        @PathVariable novelId: String
    ): ResponseEntity<ApiResponse<UserNovelInteractionDto?>> {
        val interaction = userNovelInteractionService.getUserNovelInteraction(userId, novelId)
        return ResponseEntity.ok(ApiResponse.success(interaction, "Interaction retrieved successfully"))
    }

    @GetMapping("/users/{userId}")
    fun getUserInteractions(@PathVariable userId: String): ResponseEntity<ApiResponse<List<UserNovelInteractionDto>>> {
        val interactions = userNovelInteractionService.getUserInteractions(userId)
        return ResponseEntity.ok(ApiResponse.success(interactions, "User interactions retrieved successfully"))
    }

    @GetMapping("/novels/{novelId}")
    fun getNovelInteractions(@PathVariable novelId: String): ResponseEntity<ApiResponse<List<UserNovelInteractionDto>>> {
        val interactions = userNovelInteractionService.getNovelInteractions(novelId)
        return ResponseEntity.ok(ApiResponse.success(interactions, "Novel interactions retrieved successfully"))
    }

    @PutMapping("/users/{userId}/novels/{novelId}")
    fun updateInteraction(
        @PathVariable userId: String,
        @PathVariable novelId: String,
        @Valid @RequestBody request: UserNovelInteractionUpdateRequest
    ): ResponseEntity<ApiResponse<UserNovelInteractionDto>> {
        val interaction = userNovelInteractionService.createOrUpdateInteraction(userId, novelId, request)
        return ResponseEntity.ok(ApiResponse.success(interaction, "Interaction updated successfully"))
    }

    @PostMapping("/users/{userId}/novels/{novelId}/follow")
    fun toggleFollow(
        @PathVariable userId: String,
        @PathVariable novelId: String
    ): ResponseEntity<ApiResponse<UserNovelInteractionDto>> {
        val interaction = userNovelInteractionService.toggleFollow(userId, novelId)
        return ResponseEntity.ok(ApiResponse.success(interaction, "Follow status toggled successfully"))
    }

    @PostMapping("/users/{userId}/novels/{novelId}/wishlist")
    fun toggleWishlist(
        @PathVariable userId: String,
        @PathVariable novelId: String
    ): ResponseEntity<ApiResponse<UserNovelInteractionDto>> {
        val interaction = userNovelInteractionService.toggleWishlist(userId, novelId)
        return ResponseEntity.ok(ApiResponse.success(interaction, "Wishlist status toggled successfully"))
    }

    @PostMapping("/users/{userId}/novels/{novelId}/read")
    fun updateReadingProgress(
        @PathVariable userId: String,
        @PathVariable novelId: String,
        @RequestParam chapterNumber: Int,
        @RequestParam chapterId: String
    ): ResponseEntity<ApiResponse<UserNovelInteractionDto>> {
        val interaction = userNovelInteractionService.updateReadingProgress(userId, novelId, chapterNumber, chapterId)
        return ResponseEntity.ok(ApiResponse.success(interaction, "Reading progress updated successfully"))
    }

    @GetMapping("/novels/{novelId}/follow-count")
    fun getFollowCount(@PathVariable novelId: String): ResponseEntity<ApiResponse<Long>> {
        val count = userNovelInteractionService.getFollowCount(novelId)
        return ResponseEntity.ok(ApiResponse.success(count, "Follow count retrieved successfully"))
    }

    @GetMapping("/users/{userId}/following")
    fun getUserFollowingList(@PathVariable userId: String): ResponseEntity<ApiResponse<List<UserNovelInteractionDto>>> {
        val following = userNovelInteractionService.getUserFollowingList(userId)
        return ResponseEntity.ok(ApiResponse.success(following, "User following list retrieved successfully"))
    }

    @GetMapping("/users/{userId}/wishlist")
    fun getUserWishlist(@PathVariable userId: String): ResponseEntity<ApiResponse<List<UserNovelInteractionDto>>> {
        val wishlist = userNovelInteractionService.getUserWishlist(userId)
        return ResponseEntity.ok(ApiResponse.success(wishlist, "User wishlist retrieved successfully"))
    }
}