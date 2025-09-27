package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.*
import com.miraimagiclab.novelreadingapp.dto.request.NovelCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.NovelSearchRequest
import com.miraimagiclab.novelreadingapp.dto.request.NovelUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.NovelDto
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.service.NovelService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/novels")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000", "http://127.0.0.1:8080"])
@Tag(name = "Novel Management", description = "APIs for managing novels")
class NovelController(
    private val novelService: NovelService
) {

    @PostMapping
    fun createNovel(@Valid @RequestBody request: NovelCreateRequest): ResponseEntity<ApiResponse<NovelDto>> {
        val novel = novelService.createNovel(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(novel, "Novel created successfully"))
    }

    @GetMapping("/{id}")
    fun getNovelById(@PathVariable id: String): ResponseEntity<ApiResponse<NovelDto>> {
        val novel = novelService.getNovelById(id)
        return ResponseEntity.ok(ApiResponse.success(novel, "Novel retrieved successfully"))
    }

    @PutMapping("/{id}")
    fun updateNovel(
        @PathVariable id: String,
        @Valid @RequestBody request: NovelUpdateRequest
    ): ResponseEntity<ApiResponse<NovelDto>> {
        val novel = novelService.updateNovel(id, request)
        return ResponseEntity.ok(ApiResponse.success(novel, "Novel updated successfully"))
    }

    @DeleteMapping("/{id}")
    fun deleteNovel(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        novelService.deleteNovel(id)
        return ResponseEntity.ok(ApiResponse.success("Novel deleted successfully"))
    }

    @GetMapping
    fun getAllNovels(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PageResponse<NovelDto>>> {
        val novels = novelService.getAllNovels(page, size)
        return ResponseEntity.ok(ApiResponse.success(novels, "Novels retrieved successfully"))
    }

    @PostMapping("/search")
    fun searchNovels(@RequestBody request: NovelSearchRequest): ResponseEntity<ApiResponse<PageResponse<NovelDto>>> {
        val novels = novelService.searchNovels(request)
        return ResponseEntity.ok(ApiResponse.success(novels, "Search completed successfully"))
    }

    @GetMapping("/author/{authorId}")
    fun getNovelsByAuthor(
        @PathVariable authorId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PageResponse<NovelDto>>> {
        val novels = novelService.getNovelsByAuthor(authorId, page, size)
        return ResponseEntity.ok(ApiResponse.success(novels, "Author novels retrieved successfully"))
    }

    @GetMapping("/top/view-count")
    fun getTopNovelsByViewCount(): ResponseEntity<ApiResponse<List<NovelDto>>> {
        val novels = novelService.getTopNovelsByViewCount()
        return ResponseEntity.ok(ApiResponse.success(novels, "Top novels by view count retrieved successfully"))
    }

    @GetMapping("/top/follow-count")
    fun getTopNovelsByFollowCount(): ResponseEntity<ApiResponse<List<NovelDto>>> {
        val novels = novelService.getTopNovelsByFollowCount()
        return ResponseEntity.ok(ApiResponse.success(novels, "Top novels by follow count retrieved successfully"))
    }

    @GetMapping("/top/rating")
    fun getTopNovelsByRating(): ResponseEntity<ApiResponse<List<NovelDto>>> {
        val novels = novelService.getTopNovelsByRating()
        return ResponseEntity.ok(ApiResponse.success(novels, "Top novels by rating retrieved successfully"))
    }

    @GetMapping("/recent")
    fun getRecentlyUpdatedNovels(): ResponseEntity<ApiResponse<List<NovelDto>>> {
        val novels = novelService.getRecentlyUpdatedNovels()
        return ResponseEntity.ok(ApiResponse.success(novels, "Recently updated novels retrieved successfully"))
    }


    @PostMapping("/{id}/rating")
    fun addOrUpdateRating(
        @PathVariable id: String,
        @RequestParam userId: String,
        @RequestParam rating: Double
    ): ResponseEntity<ApiResponse<NovelDto>> {
        val novel = novelService.addOrUpdateRating(id, userId, rating)
        return ResponseEntity.ok(ApiResponse.success(novel, "Rating updated successfully"))
    }
}
