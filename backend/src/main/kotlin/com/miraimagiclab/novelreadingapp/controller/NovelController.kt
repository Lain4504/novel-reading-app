package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.*
import com.miraimagiclab.novelreadingapp.dto.request.NovelCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.NovelSearchRequest
import com.miraimagiclab.novelreadingapp.dto.request.NovelUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.NovelDto
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.service.NovelService
import com.miraimagiclab.novelreadingapp.enumeration.CategoryEnum
import com.miraimagiclab.novelreadingapp.enumeration.NovelStatusEnum
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Max
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import com.miraimagiclab.novelreadingapp.service.CloudinaryStorageService

@RestController
@RequestMapping("/novels")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000", "http://127.0.0.1:8080"])
@Tag(name = "Novel Management", description = "APIs for managing novels")
class NovelController(
    private val novelService: NovelService,
    private val cloudinaryStorageService: CloudinaryStorageService
) {

    @Operation(
        summary = "Create a new novel",
        description = "Create a new novel with individual form parameters and optional cover image."
    )
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createNovel(
        @Valid @RequestParam("title", required = true)
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        @Schema(description = "Novel title")
        title: String,
        
        @Valid @RequestParam("description", required = true)
        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
        @Schema(description = "Novel description")
        description: String,
        
        @Valid @RequestParam("authorName", required = true)
        @NotBlank(message = "Author name is required")
        @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
        @Schema(description = "Author name")
        authorName: String,
        
        @RequestParam("authorId", required = false)
        @Size(min = 1, max = 100, message = "Author ID must be between 1 and 100 characters")
        @Schema(description = "Author ID (optional)")
        authorId: String? = null,
        
        @RequestParam("categories", required = true)
        @NotEmpty(message = "At least one category is required")
        @Schema(description = "Comma-separated list of categories")
        categories: String,
        
        @RequestParam("status", required = false)
        @Schema(description = "Novel status", defaultValue = "DRAFT")
        status: NovelStatusEnum = NovelStatusEnum.DRAFT,
        
        @RequestParam("isR18", required = false)
        @Schema(description = "Whether the novel is R18 content", defaultValue = "false")
        isR18: Boolean = false,
        
        @RequestPart(name = "coverImage", required = false) 
        @Schema(description = "Cover image file (optional)", type = "string", format = "binary")
        coverImage: MultipartFile?,
        
        @RequestParam("coverUrl", required = false)
        @Schema(description = "Cover image URL (optional, used when image is already uploaded)")
        coverUrl: String? = null
    ): ResponseEntity<ApiResponse<NovelDto>> {
        // Parse categories from comma-separated string
        val categorySet = categories.split(",")
            .map { it.trim().uppercase() }
            .mapNotNull { categoryName ->
                try {
                    CategoryEnum.valueOf(categoryName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            .toSet()
        
        if (categorySet.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid categories provided"))
        }
        
        // Create NovelCreateRequest object
        val request = NovelCreateRequest(
            title = title,
            description = description,
            authorName = authorName,
            authorId = authorId,
            categories = categorySet,
            status = status,
            isR18 = isR18
        )
        
        // Priority: coverUrl > coverImage upload
        val finalRequest = if (!coverUrl.isNullOrBlank()) {
            request.copy(coverImage = coverUrl)
        } else if (coverImage != null && !coverImage.isEmpty) {
            val url = cloudinaryStorageService.uploadAndGetUrl(coverImage, folder = "novels/covers")
            request.copy(coverImage = url)
        } else request
        
        val novel = novelService.createNovel(finalRequest)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(novel, "Novel created successfully"))
    }

    @GetMapping("/{id}")
    fun getNovelById(@PathVariable id: String): ResponseEntity<ApiResponse<NovelDto>> {
        val novel = novelService.getNovelById(id)
        return ResponseEntity.ok(ApiResponse.success(novel, "Novel retrieved successfully"))
    }

    @PutMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateNovel(
        @PathVariable id: String,
        
        @RequestParam("title", required = false)
        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        @Schema(description = "Novel title (optional)")
        title: String? = null,
        
        @RequestParam("description", required = false)
        @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
        @Schema(description = "Novel description (optional)")
        description: String? = null,
        
        @RequestParam("authorName", required = false)
        @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
        @Schema(description = "Author name (optional)")
        authorName: String? = null,
        
        @RequestParam("authorId", required = false)
        @Size(min = 1, max = 100, message = "Author ID must be between 1 and 100 characters")
        @Schema(description = "Author ID (optional)")
        authorId: String? = null,
        
        @RequestParam("categories", required = false)
        @Schema(description = "Comma-separated list of categories (optional)")
        categories: String? = null,
        
        @RequestParam("rating", required = false)
        @Schema(description = "Novel rating (optional, must be between 0 and 5)")
        rating: Double? = null,
        
        @RequestParam("wordCount", required = false)
        @Schema(description = "Word count (optional)")
        wordCount: Int? = null,
        
        @RequestParam("chapterCount", required = false)
        @Schema(description = "Chapter count (optional)")
        chapterCount: Int? = null,
        
        @RequestParam("status", required = false)
        @Schema(description = "Novel status (optional)")
        status: NovelStatusEnum? = null,
        
        @RequestParam("isR18", required = false)
        @Schema(description = "Whether the novel is R18 content (optional)")
        isR18: Boolean? = null,
        
        @RequestParam("coverUrl", required = false)
        @Schema(description = "Cover image URL (optional)")
        coverUrl: String? = null,
        
        @RequestPart(name = "coverImage", required = false) 
        @Schema(description = "Cover image file (optional)", type = "string", format = "binary")
        coverImage: MultipartFile?,
        
        @RequestParam("coverUrl", required = false)
        @Schema(description = "Cover image URL (optional, used when image is already uploaded)")
        coverUrl: String? = null
    ): ResponseEntity<ApiResponse<NovelDto>> {
        // Parse categories from comma-separated string if provided
        val categorySet = if (categories != null && categories.isNotBlank()) {
            val parsed = categories.split(",")
                .map { it.trim().uppercase() }
                .mapNotNull { categoryName ->
                    try {
                        CategoryEnum.valueOf(categoryName)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
                .toSet()
            
            if (parsed.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid categories provided"))
            }
            parsed
        } else null
        
        // Create NovelUpdateRequest object
        val request = NovelUpdateRequest(
            title = title,
            description = description,
            authorName = authorName,
            authorId = authorId,
            categories = categorySet,
            rating = rating,
            wordCount = wordCount,
            chapterCount = chapterCount,
            status = status,
            isR18 = isR18
        )
        
        // Priority: coverUrl > coverImage upload
        val finalRequest = if (!coverUrl.isNullOrBlank()) {
            request.copy(coverImage = coverUrl)
        } else if (coverImage != null && !coverImage.isEmpty) {
            val url = cloudinaryStorageService.uploadAndGetUrl(coverImage, folder = "novels/covers")
            request.copy(coverImage = url)
        } else request
        
        val novel = novelService.updateNovel(id, finalRequest)
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

    @GetMapping("/test")
    fun testEndpoint(): ResponseEntity<ApiResponse<String>> {
        return ResponseEntity.ok(ApiResponse.success("Backend is working!", "Test successful"))
    }

    @PostMapping("/seed")
    fun seedTestData(): ResponseEntity<ApiResponse<String>> {
        try {
            novelService.seedTestData()
            return ResponseEntity.ok(ApiResponse.success("Test data seeded successfully!", "Seeding successful"))
        } catch (e: Exception) {
            return ResponseEntity.ok(ApiResponse.error("Failed to seed data: ${e.message}"))
        }
    }
}
