package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.dto.request.ChapterRequestDto
import com.miraimagiclab.novelreadingapp.dto.response.ChapterResponseDto
import com.miraimagiclab.novelreadingapp.model.Chapter
import com.miraimagiclab.novelreadingapp.repository.ChapterRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable

@Service
@Transactional
class ChapterService (
    private val chapterRepository: ChapterRepository
){
    fun createChapter(request: ChapterRequestDto): ChapterResponseDto {
        if (chapterRepository.existsByNovelId(request.novelId)) {
            throw Exception("Chapter for Novel ID '${request.novelId}' already exists")
        }
        val chapter = Chapter(
            novelId = request.novelId,
            chapterTitle = request.chapterTitle,
            content = request.content,
            wordCount = request.content.split("\\s+".toRegex()).size,
            viewCount = 0,
            chapterNumber = chapterRepository.countChapterByNovelId(request.novelId) + 1
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

    fun updateChapter(chapterId: String, request: ChapterRequestDto): ChapterResponseDto {
        val existingChapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }
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
}