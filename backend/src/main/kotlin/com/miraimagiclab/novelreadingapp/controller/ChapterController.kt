package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.dto.request.ChapterRequestDto
import com.miraimagiclab.novelreadingapp.dto.response.ChapterResponseDto
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/chapters")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000", "http://127.0.0.1:8080"])
class ChapterController(
    private val chapterService: ChapterService
) {
    @PostMapping
    fun createChapter(@Valid @RequestBody requestDto: ChapterRequestDto): ResponseEntity<ApiResponse<ChapterResponseDto>> {
        val chapter = chapterService.createChapter(requestDto)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(chapter, "Chapter created successfully"))
    }

    @PutMapping("/{chapterId}")
    fun updateChapter(
        @PathVariable("chapterId") chapterId: String,
        @Valid @RequestBody requestDto: ChapterRequestDto
    ): ResponseEntity<ApiResponse<ChapterResponseDto>?> {
        val chapter = chapterService.updateChapter(chapterId, requestDto)
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

}