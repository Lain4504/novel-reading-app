package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.mapper.ChapterMapper
import com.miraimagiclab.novelreadingapp.data.remote.api.ChapterApiService
import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.ChapterDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import com.miraimagiclab.novelreadingapp.domain.model.Chapter
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ChapterRepositoryImplTest {

    private lateinit var chapterApiService: ChapterApiService
    private lateinit var chapterRepository: ChapterRepositoryImpl

    @Before
    fun setup() {
        chapterApiService = mockk()
        chapterRepository = ChapterRepositoryImpl(chapterApiService)
    }

    @Test
    fun `getChapterById should return chapter from API when not in cache`() = runTest {
        // Given
        val chapterId = "chapter_123"
        val chapterDto = ChapterDto(
            id = chapterId,
            novelId = "novel_456",
            chapterTitle = "Test Chapter",
            chapterNumber = 1,
            content = "Test content",
            wordCount = 100,
            viewCount = 50,
            createdAt = "2023-01-01T00:00:00",
            updatedAt = "2023-01-01T00:00:00"
        )
        val apiResponse = ApiResponse(success = true, data = chapterDto, message = "Success")

        coEvery { chapterApiService.getChapterById(chapterId) } returns apiResponse

        // When
        val result = chapterRepository.getChapterById(chapterId).first()

        // Then
        assertNotNull(result)
        assertEquals(chapterId, result?.id)
        assertEquals("Test Chapter", result?.chapterTitle)
        assertEquals("Test content", result?.content)
    }

    @Test
    fun `getChapterById should return null when API fails`() = runTest {
        // Given
        val chapterId = "chapter_123"
        val apiResponse = ApiResponse<ChapterDto>(success = false, data = null, message = "Not found")

        coEvery { chapterApiService.getChapterById(chapterId) } returns apiResponse

        // When
        val result = chapterRepository.getChapterById(chapterId).first()

        // Then
        assertNull(result)
    }

    @Test
    fun `getChaptersByNovelId should return chapters from API when not in cache`() = runTest {
        // Given
        val novelId = "novel_456"
        val chapterDtos = listOf(
            ChapterDto(
                id = "chapter_1",
                novelId = novelId,
                chapterTitle = "Chapter 1",
                chapterNumber = 1,
                content = "Content 1",
                wordCount = 100,
                viewCount = 50,
                createdAt = "2023-01-01T00:00:00",
                updatedAt = "2023-01-01T00:00:00"
            ),
            ChapterDto(
                id = "chapter_2",
                novelId = novelId,
                chapterTitle = "Chapter 2",
                chapterNumber = 2,
                content = "Content 2",
                wordCount = 120,
                viewCount = 60,
                createdAt = "2023-01-02",
                updatedAt = "2023-01-02"
            )
        )
        val pageResponse = PageResponse(
            content = chapterDtos,
            totalElements = 2,
            totalPages = 1,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 2
        )
        val apiResponse = ApiResponse(success = true, data = pageResponse, message = "Success")

        coEvery { chapterApiService.getChaptersByNovelId(novelId) } returns apiResponse

        // When
        val result = chapterRepository.getChaptersByNovelId(novelId).first()

        // Then
        assertEquals(2, result.size)
        assertEquals("chapter_1", result[0].id)
        assertEquals("Chapter 1", result[0].chapterTitle)
        assertEquals("chapter_2", result[1].id)
        assertEquals("Chapter 2", result[1].chapterTitle)
    }

    @Test
    fun `getChaptersByNovelId should return empty list when API fails`() = runTest {
        // Given
        val novelId = "novel_456"
        val apiResponse = ApiResponse<PageResponse<ChapterDto>>(success = false, data = null, message = "Not found")

        coEvery { chapterApiService.getChaptersByNovelId(novelId) } returns apiResponse

        // When
        val result = chapterRepository.getChaptersByNovelId(novelId).first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getChapterByIdSync should return cached chapter when available`() = runTest {
        // Given
        val chapterId = "chapter_123"
        val chapterDto = ChapterDto(
            id = chapterId,
            novelId = "novel_456",
            chapterTitle = "Test Chapter",
            chapterNumber = 1,
            content = "Test content",
            wordCount = 100,
            viewCount = 50,
            createdAt = "2023-01-01T00:00:00",
            updatedAt = "2023-01-01T00:00:00"
        )
        val apiResponse = ApiResponse(success = true, data = chapterDto, message = "Success")

        coEvery { chapterApiService.getChapterById(chapterId) } returns apiResponse

        // First call to populate cache
        chapterRepository.getChapterById(chapterId).first()

        // When - second call should use cache
        val result = chapterRepository.getChapterByIdSync(chapterId)

        // Then
        assertNotNull(result)
        assertEquals(chapterId, result?.id)
        assertEquals("Test Chapter", result?.chapterTitle)
    }

    @Test
    fun `refreshChapter should update cache with fresh data`() = runTest {
        // Given
        val chapterId = "chapter_123"
        val chapterDto = ChapterDto(
            id = chapterId,
            novelId = "novel_456",
            chapterTitle = "Updated Chapter",
            chapterNumber = 1,
            content = "Updated content",
            wordCount = 150,
            viewCount = 75,
            createdAt = "2023-01-01T00:00:00",
            updatedAt = "2023-01-02"
        )
        val apiResponse = ApiResponse(success = true, data = chapterDto, message = "Success")

        coEvery { chapterApiService.getChapterById(chapterId) } returns apiResponse

        // When
        chapterRepository.refreshChapter(chapterId)
        val result = chapterRepository.getChapterByIdSync(chapterId)

        // Then
        assertNotNull(result)
        assertEquals("Updated Chapter", result?.chapterTitle)
        assertEquals("Updated content", result?.content)
    }
}
