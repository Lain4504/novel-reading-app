package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.dto.request.ChapterCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.ChapterUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.ChapterResponseDto
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.model.Chapter
import com.miraimagiclab.novelreadingapp.repository.ChapterRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable

@Service
@Transactional
class ChapterService (
    private val chapterRepository: ChapterRepository,
    private val novelService: NovelService
){
    fun createChapter(novelId: String, request: ChapterCreateRequest): ChapterResponseDto {
        val chapter = Chapter(
            novelId = novelId,
            chapterTitle = request.chapterTitle,
            content = request.content,
            wordCount = request.content.split("\\s+".toRegex()).size,
            viewCount = 0,
            chapterNumber = chapterRepository.countByNovelId(novelId) + 1
        )
        val savedChapter = chapterRepository.save(chapter)
        return ChapterResponseDto(
            id = savedChapter.id!!,
            novelId = savedChapter.novelId,
            chapterTitle = savedChapter.chapterTitle,
            chapterNumber = savedChapter.chapterNumber,
            content = savedChapter.content,
            wordCount = savedChapter.wordCount,
            viewCount = savedChapter.viewCount,
            createdAt = savedChapter.createdAt.toString(),
            updatedAt = savedChapter.updatedAt.toString()
        )
    }

    fun updateChapter(novelId: String, chapterId: String, request: ChapterUpdateRequest): ChapterResponseDto {
        val existingChapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }
        
        // Verify that the chapter belongs to the specified novel
        if (existingChapter.novelId != novelId) {
            throw Exception("Chapter with ID '$chapterId' does not belong to novel '$novelId'")
        }
        
        val updatedChapter = existingChapter.copy(
            chapterTitle = request.chapterTitle,
            content = request.content,
            wordCount = request.content.split("\\s+".toRegex()).size
        )
        val savedChapter = chapterRepository.save(updatedChapter)
        return ChapterResponseDto(
            id = savedChapter.id!!,
            novelId = savedChapter.novelId,
            chapterTitle = savedChapter.chapterTitle,
            chapterNumber = savedChapter.chapterNumber,
            content = savedChapter.content,
            wordCount = savedChapter.wordCount,
            viewCount = savedChapter.viewCount,
            createdAt = savedChapter.createdAt.toString(),
            updatedAt = savedChapter.updatedAt.toString()
        )
    }

    fun deleteChapter(@PathVariable("chapterId") chapterId: String): ResponseEntity<Nothing> {
        if (!chapterRepository.existsById(chapterId)) {
            throw Exception("Chapter with ID '$chapterId' not found")
        }
        chapterRepository.deleteById(chapterId)
        return ResponseEntity.noContent().build()
    }
    fun getChapterById(chapterId: String): ChapterResponseDto {
        val chapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }
        return ChapterResponseDto(
            id = chapter.id!!,
            novelId = chapter.novelId,
            chapterTitle = chapter.chapterTitle,
            chapterNumber = chapter.chapterNumber,
            content = chapter.content,
            wordCount = chapter.wordCount,
            viewCount = chapter.viewCount,
            createdAt = chapter.createdAt.toString(),
            updatedAt = chapter.updatedAt.toString()
        )
    }

    fun getChapterByNovelAndChapterId(novelId: String, chapterId: String): ChapterResponseDto {
        val chapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }
        
        // Verify that the chapter belongs to the specified novel
        if (chapter.novelId != novelId) {
            throw Exception("Chapter with ID '$chapterId' does not belong to novel '$novelId'")
        }
        
        return ChapterResponseDto(
            id = chapter.id!!,
            novelId = chapter.novelId,
            chapterTitle = chapter.chapterTitle,
            chapterNumber = chapter.chapterNumber,
            content = chapter.content,
            wordCount = chapter.wordCount,
            viewCount = chapter.viewCount,
            createdAt = chapter.createdAt.toString(),
            updatedAt = chapter.updatedAt.toString()
        )
    }

    @Transactional(readOnly = true)
    fun getChaptersByNovelId(novelId: String, page: Int = 0, size: Int = 20, sortDirection: String = "asc"): PageResponse<ChapterResponseDto> {
        val sort = Sort.by(
            if (sortDirection == "asc") Sort.Direction.ASC else Sort.Direction.DESC,
            "chapterNumber"
        )
        val pageable: Pageable = PageRequest.of(page, size, sort)
        val chapters = chapterRepository.findByNovelId(novelId, pageable)
        
        val chapterDtos = chapters.content.map { chapter ->
            ChapterResponseDto(
                id = chapter.id!!,
                novelId = chapter.novelId,
                chapterTitle = chapter.chapterTitle,
                chapterNumber = chapter.chapterNumber,
                content = chapter.content,
                wordCount = chapter.wordCount,
                viewCount = chapter.viewCount,
                createdAt = chapter.createdAt.toString(),
                updatedAt = chapter.updatedAt.toString()
            )
        }
        
        return PageResponse(
            content = chapterDtos,
            page = chapters.number,
            size = chapters.size,
            totalElements = chapters.totalElements,
            totalPages = chapters.totalPages,
            first = chapters.isFirst,
            last = chapters.isLast,
            numberOfElements = chapters.numberOfElements
        )
    }

    fun reorderChapters(novelId: String, newOrder: List<String>): List<ChapterResponseDto> {
        val chapters = chapterRepository.findByNovelId(novelId)
        if (chapters.size != newOrder.size || !chapters.all { newOrder.contains(it.id) }) {
            throw Exception("Invalid chapter order")
        }
        val chapterMap = chapters.associateBy { it.id }
        val reorderedChapters = newOrder.mapIndexed { index, chapterId ->
            val chapter = chapterMap[chapterId] ?: throw Exception("Chapter with ID '$chapterId' not found")
            chapter.copy(chapterNumber = index + 1)
        }
        chapterRepository.saveAll(reorderedChapters)
        return reorderedChapters.map { chapter ->
            ChapterResponseDto(
                id = chapter.id!!,
                novelId = chapter.novelId,
                chapterTitle = chapter.chapterTitle,
                chapterNumber = chapter.chapterNumber,
                content = chapter.content,
                wordCount = chapter.wordCount,
                viewCount = chapter.viewCount,
                createdAt = chapter.createdAt.toString(),
                updatedAt = chapter.updatedAt.toString()
            )
        }
    }

    fun incrementViewCount(chapterId: String): ChapterResponseDto {
        val chapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }

        val updatedChapter = chapter.copy(
            viewCount = chapter.viewCount + 1,
            updatedAt = java.time.LocalDateTime.now()
        )

        val savedChapter = chapterRepository.save(updatedChapter)
        
        // Also increment the novel's view count
        novelService.incrementViewCount(chapter.novelId)
        
        return ChapterResponseDto(
            id = savedChapter.id!!,
            novelId = savedChapter.novelId,
            chapterTitle = savedChapter.chapterTitle,
            chapterNumber = savedChapter.chapterNumber,
            content = savedChapter.content,
            wordCount = savedChapter.wordCount,
            viewCount = savedChapter.viewCount,
            createdAt = savedChapter.createdAt.toString(),
            updatedAt = savedChapter.updatedAt.toString()
        )
    }
}