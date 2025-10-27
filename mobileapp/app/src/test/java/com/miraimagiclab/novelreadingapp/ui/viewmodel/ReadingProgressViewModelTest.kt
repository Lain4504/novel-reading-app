package com.miraimagiclab.novelreadingapp.ui.viewmodel

import com.miraimagiclab.novelreadingapp.data.repository.ReadingProgressRepository
import com.miraimagiclab.novelreadingapp.domain.model.ReadingProgress
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class ReadingProgressViewModelTest {

    private lateinit var viewModel: ReadingProgressViewModel
    private lateinit var mockRepository: ReadingProgressRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        viewModel = ReadingProgressViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getReadingProgress should load progress from repository`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        val expectedProgress = ReadingProgress(
            userId = userId,
            novelId = novelId,
            currentChapterId = "chapter789",
            currentChapterNumber = 1,
            lastReadAt = Date()
        )
        
        coEvery { mockRepository.getReadingProgress(userId, novelId) } returns expectedProgress

        // When
        viewModel.getReadingProgress(userId, novelId)

        // Then
        assert(viewModel.currentProgress.value == expectedProgress)
        assert(!viewModel.isLoading.value)
        assert(viewModel.error.value == null)
    }

    @Test
    fun `updateReadingProgress should update progress correctly`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        val chapterId = "chapter789"
        val chapterNumber = 5
        
        val updatedProgress = ReadingProgress(
            userId = userId,
            novelId = novelId,
            currentChapterId = chapterId,
            currentChapterNumber = chapterNumber,
            lastReadAt = Date()
        )
        
        coEvery { mockRepository.updateReadingProgress(userId, novelId, chapterId, chapterNumber) } returns updatedProgress

        // When
        viewModel.updateReadingProgress(userId, novelId, chapterId, chapterNumber)

        // Then
        coVerify { mockRepository.updateReadingProgress(userId, novelId, chapterId, chapterNumber) }
        assert(viewModel.currentProgress.value == updatedProgress)
        assert(!viewModel.isLoading.value)
        assert(viewModel.error.value == null)
    }

    @Test
    fun `getReadingProgress should handle repository error`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        val errorMessage = "Network error"
        
        coEvery { mockRepository.getReadingProgress(userId, novelId) } throws Exception(errorMessage)

        // When
        viewModel.getReadingProgress(userId, novelId)

        // Then
        assert(viewModel.currentProgress.value == null)
        assert(!viewModel.isLoading.value)
        assert(viewModel.error.value == errorMessage)
    }

    @Test
    fun `updateReadingProgress should handle repository error`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        val chapterId = "chapter789"
        val chapterNumber = 5
        val errorMessage = "Update failed"
        
        coEvery { mockRepository.updateReadingProgress(userId, novelId, chapterId, chapterNumber) } throws Exception(errorMessage)

        // When
        viewModel.updateReadingProgress(userId, novelId, chapterId, chapterNumber)

        // Then
        assert(!viewModel.isLoading.value)
        assert(viewModel.error.value == errorMessage)
    }

    @Test
    fun `clearError should clear error state`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        
        coEvery { mockRepository.getReadingProgress(userId, novelId) } throws Exception("Test error")
        viewModel.getReadingProgress(userId, novelId)
        assert(viewModel.error.value != null)

        // When
        viewModel.clearError()

        // Then
        assert(viewModel.error.value == null)
    }

    @Test
    fun `isCompleted should return true when chapter data is available`() = runTest {
        // Given
        val userId = "user123"
        val novelId = "novel456"
        val progress = ReadingProgress(
            userId = userId,
            novelId = novelId,
            currentChapterId = "chapter789",
            currentChapterNumber = 1,
            lastReadAt = Date()
        )
        
        coEvery { mockRepository.getReadingProgress(userId, novelId) } returns progress

        // When
        viewModel.getReadingProgress(userId, novelId)

        // Then
        assert(viewModel.currentProgress.value?.isCompleted == true)
    }
}
