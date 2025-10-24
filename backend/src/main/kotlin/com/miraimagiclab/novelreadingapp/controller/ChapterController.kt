package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.dto.request.ChapterCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.ChapterUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.ChapterResponseDto
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.service.ChapterService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/chapters")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000", "http://127.0.0.1:8080"])
class ChapterController(
    private val chapterService: ChapterService
) {
    @PostMapping("/novels/{novelId}")
    fun createChapter(
        @PathVariable("novelId") novelId: String,
        @Valid @RequestBody requestDto: ChapterCreateRequest
    ): ResponseEntity<ApiResponse<ChapterResponseDto>> {
        val chapter = chapterService.createChapter(novelId, requestDto)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(chapter, "Chapter created successfully"))
    }

    @PutMapping("/novels/{novelId}/{chapterId}")
    fun updateChapter(
        @PathVariable("novelId") novelId: String,
        @PathVariable("chapterId") chapterId: String,
        @Valid @RequestBody requestDto: ChapterUpdateRequest
    ): ResponseEntity<ApiResponse<ChapterResponseDto>?> {
        val chapter = chapterService.updateChapter(novelId, chapterId, requestDto)
        return ResponseEntity.ok(ApiResponse.success(chapter, "Chapter updated successfully"))
    }

    @DeleteMapping("/{chapterId}")
    fun deleteChapter(@PathVariable("chapterId") chapterId: String): ResponseEntity<ApiResponse<Nothing>> {
        // Assuming a deleteChapter method exists in ChapterService
        chapterService.deleteChapter(chapterId)
        return ResponseEntity.ok(ApiResponse.success("Chapter deleted successfully"))
    }

    @GetMapping("/{chapterId}")
    fun getChapterById(@PathVariable("chapterId") chapterId: String): ResponseEntity<ApiResponse<ChapterResponseDto>> {
        val chapter = chapterService.getChapterById(chapterId)
        return ResponseEntity.ok(ApiResponse.success(chapter, "Chapter retrieved successfully"))
    }

    @GetMapping("/novels/{novelId}")
    fun getChaptersByNovelId(
        @PathVariable("novelId") novelId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "asc") sortDirection: String
    ): ResponseEntity<ApiResponse<PageResponse<ChapterResponseDto>>> {
        val chapters = chapterService.getChaptersByNovelId(novelId, page, size, sortDirection)
        return ResponseEntity.ok(ApiResponse.success(chapters, "Chapters retrieved successfully"))
    }

    @PostMapping("/reorder/{novelId}")
    fun reorderChapters(
        @PathVariable("novelId") novelId: String,
        @RequestBody newOrder: List<String>
    ): ResponseEntity<ApiResponse<String>> {
        chapterService.reorderChapters(novelId, newOrder)
        return ResponseEntity.ok(ApiResponse.success("Chapters reordered successfully"))
    }

    @PostMapping("/{chapterId}/increment-view")
    fun incrementChapterViewCount(@PathVariable("chapterId") chapterId: String): ResponseEntity<ApiResponse<ChapterResponseDto>> {
        val chapter = chapterService.incrementViewCount(chapterId)
        return ResponseEntity.ok(ApiResponse.success(chapter, "Chapter view count incremented successfully"))
    }

}