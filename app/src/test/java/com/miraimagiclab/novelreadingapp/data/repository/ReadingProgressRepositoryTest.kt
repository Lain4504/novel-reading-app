package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.mapper.ReadingProgressMapper
import com.miraimagiclab.novelreadingapp.data.remote.api.UserNovelInteractionApiService
import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserNovelInteractionDto
import com.miraimagiclab.novelreadingapp.domain.model.ReadingProgress
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class ReadingProgressRepositoryTest {

    private lateinit var repository: ReadingProgressRepository
    private lateinit var mockApiService: UserNovelInteractionApiService

    @Before
    fun setup() {
        mockApiService = mockk()
        repository = ReadingProgressRepository(mockApiService)
    }

    @Test
    fun `getReadingProgress should return mapped progress when API call succeeds`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        val dto = UserNovelInteractionDto(
            id = "interaction123",
            userId = userId,
            novelId = novelId,
            hasFollowing = false,
            inWishlist = false,
            notify = false,
            currentChapterNumber = 5,
            currentChapterId = "chapter789",
            lastReadAt = LocalDateTime.now(),
            totalChapterReads = 10,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val apiResponse = ApiResponse(
            success = true,
            message = "Success",
            data = dto,
            timestamp = "2024-01-01T00:00:00"
        )
        
        coEvery { mockApiService.getUserNovelInteraction(userId, novelId) } returns apiResponse

        // When
        val result = repository.getReadingProgress(userId, novelId)

        // Then
        assert(result != null)
        assert(result?.userId == userId)
        assert(result?.novelId == novelId)
        assert(result?.currentChapterId == "chapter789")
        assert(result?.currentChapterNumber == 5)
        coVerify { mockApiService.getUserNovelInteraction(userId, novelId) }
    }

    @Test
    fun `getReadingProgress should return null when API call fails`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        val apiResponse = ApiResponse<UserNovelInteractionDto?>(
            success = false,
            message = "Not found",
            data = null,
            timestamp = "2024-01-01T00:00:00"
        )
        
        coEvery { mockApiService.getUserNovelInteraction(userId, novelId) } returns apiResponse

        // When
        val result = repository.getReadingProgress(userId, novelId)

        // Then
        assert(result == null)
        coVerify { mockApiService.getUserNovelInteraction(userId, novelId) }
    }

    @Test
    fun `getReadingProgress should return null when API throws exception`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        
        coEvery { mockApiService.getUserNovelInteraction(userId, novelId) } throws Exception("Network error")

        // When
        val result = repository.getReadingProgress(userId, novelId)

        // Then
        assert(result == null)
        coVerify { mockApiService.getUserNovelInteraction(userId, novelId) }
    }

    @Test
    fun `updateReadingProgress should return mapped progress when API call succeeds`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        val chapterId = "chapter789"
        val chapterNumber = 5
        val dto = UserNovelInteractionDto(
            id = "interaction123",
            userId = userId,
            novelId = novelId,
            hasFollowing = false,
            inWishlist = false,
            notify = false,
            currentChapterNumber = chapterNumber,
            currentChapterId = chapterId,
            lastReadAt = LocalDateTime.now(),
            totalChapterReads = 11,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val apiResponse = ApiResponse(
            success = true,
            message = "Updated successfully",
            data = dto,
            timestamp = "2024-01-01T00:00:00"
        )
        
        coEvery { 
            mockApiService.updateReadingProgress(userId, novelId, chapterNumber, chapterId) 
        } returns apiResponse

        // When
        val result = repository.updateReadingProgress(userId, novelId, chapterId, chapterNumber)

        // Then
        assert(result != null)
        assert(result?.userId == userId)
        assert(result?.novelId == novelId)
        assert(result?.currentChapterId == chapterId)
        assert(result?.currentChapterNumber == chapterNumber)
        coVerify { mockApiService.updateReadingProgress(userId, novelId, chapterNumber, chapterId) }
    }

    @Test
    fun `updateReadingProgress should return null when API call fails`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        val chapterId = "chapter789"
        val chapterNumber = 5
        val apiResponse = ApiResponse<UserNovelInteractionDto>(
            success = false,
            message = "Update failed",
            data = null,
            timestamp = "2024-01-01T00:00:00"
        )
        
        coEvery { 
            mockApiService.updateReadingProgress(userId, novelId, chapterNumber, chapterId) 
        } returns apiResponse

        // When
        val result = repository.updateReadingProgress(userId, novelId, chapterId, chapterNumber)

        // Then
        assert(result == null)
        coVerify { mockApiService.updateReadingProgress(userId, novelId, chapterNumber, chapterId) }
    }

    @Test
    fun `updateReadingProgress should return null when API throws exception`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        val chapterId = "chapter789"
        val chapterNumber = 5
        
        coEvery { 
            mockApiService.updateReadingProgress(userId, novelId, chapterNumber, chapterId) 
        } throws Exception("Network error")

        // When
        val result = repository.updateReadingProgress(userId, novelId, chapterId, chapterNumber)

        // Then
        assert(result == null)
        coVerify { mockApiService.updateReadingProgress(userId, novelId, chapterNumber, chapterId) }
    }
}