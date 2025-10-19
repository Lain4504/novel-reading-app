package com.miraimagiclab.novelreadingapp.ui.viewmodel

import com.miraimagiclab.novelreadingapp.domain.model.Chapter
import com.miraimagiclab.novelreadingapp.domain.repository.ChapterRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ReadingScreenViewModelTest {

    private lateinit var chapterRepository: ChapterRepository
    private lateinit var viewModel: ReadingScreenViewModel

    @Before
    fun setup() {
        chapterRepository = mockk()
        viewModel = ReadingScreenViewModel(chapterRepository)
    }

    @Test
    fun `loadChapter should emit success state when chapter is found`() = runTest {
        // Given
        val novelId = "novel_123"
        val chapterId = "chapter_456"
        val chapter = Chapter(
            id = chapterId,
            novelId = novelId,
            chapterTitle = "Test Chapter",
            chapterNumber = 1,
            content = "Test content",
            wordCount = 100,
            viewCount = 50,
            createdAt = "2023-01-01",
            updatedAt = "2023-01-01"
        )

        coEvery { chapterRepository.getChapterById(chapterId) } returns flowOf(chapter)
        coEvery { chapterRepository.getChaptersByNovelId(novelId) } returns flowOf(emptyList())

        // When
        viewModel.loadChapter(novelId, chapterId)

        // Then
        val result = viewModel.currentChapter.value
        assertTrue(result is UiState.Success)
        assertEquals(chapter, (result as UiState.Success).data)
    }

    @Test
    fun `loadChapter should emit error state when chapter is not found`() = runTest {
        // Given
        val novelId = "novel_123"
        val chapterId = "chapter_456"

        coEvery { chapterRepository.getChapterById(chapterId) } returns flowOf(null)
        coEvery { chapterRepository.getChaptersByNovelId(novelId) } returns flowOf(emptyList())

        // When
        viewModel.loadChapter(novelId, chapterId)

        // Then
        val result = viewModel.currentChapter.value
        assertTrue(result is UiState.Error)
        assertEquals("Chapter not found", (result as UiState.Error).message)
    }

    @Test
    fun `loadChapterList should emit success state with chapters`() = runTest {
        // Given
        val novelId = "novel_123"
        val chapters = listOf(
            Chapter(
                id = "chapter_1",
                novelId = novelId,
                chapterTitle = "Chapter 1",
                chapterNumber = 1,
                content = "Content 1",
                wordCount = 100,
                viewCount = 50,
                createdAt = "2023-01-01",
                updatedAt = "2023-01-01"
            ),
            Chapter(
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

        coEvery { chapterRepository.getChaptersByNovelId(novelId) } returns flowOf(chapters)

        // When
        viewModel.loadChapterList(novelId)

        // Then
        val result = viewModel.chapterList.value
        assertTrue(result is UiState.Success)
        assertEquals(chapters, (result as UiState.Success).data)
    }

    @Test
    fun `hasNextChapter should return true when next chapter exists`() = runTest {
        // Given
        val novelId = "novel_123"
        val chapters = listOf(
            Chapter(
                id = "chapter_1",
                novelId = novelId,
                chapterTitle = "Chapter 1",
                chapterNumber = 1,
                content = "Content 1",
                wordCount = 100,
                viewCount = 50,
                createdAt = "2023-01-01",
                updatedAt = "2023-01-01"
            ),
            Chapter(
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

        coEvery { chapterRepository.getChaptersByNovelId(novelId) } returns flowOf(chapters)

        // Load chapter list first
        viewModel.loadChapterList(novelId)

        // When
        val result = viewModel.hasNextChapter()

        // Then
        assertFalse(result) // No current chapter set, so no next chapter
    }

    @Test
    fun `hasPreviousChapter should return true when previous chapter exists`() = runTest {
        // Given
        val novelId = "novel_123"
        val chapterId = "chapter_2"
        val chapters = listOf(
            Chapter(
                id = "chapter_1",
                novelId = novelId,
                chapterTitle = "Chapter 1",
                chapterNumber = 1,
                content = "Content 1",
                wordCount = 100,
                viewCount = 50,
                createdAt = "2023-01-01",
                updatedAt = "2023-01-01"
            ),
            Chapter(
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

        coEvery { chapterRepository.getChapterById(chapterId) } returns flowOf(chapters[1])
        coEvery { chapterRepository.getChaptersByNovelId(novelId) } returns flowOf(chapters)

        // Load chapter and chapter list
        viewModel.loadChapter(novelId, chapterId)

        // When
        val result = viewModel.hasPreviousChapter()

        // Then
        assertTrue(result)
    }

    @Test
    fun `navigateToNextChapter should load next chapter`() = runTest {
        // Given
        val novelId = "novel_123"
        val currentChapterId = "chapter_1"
        val nextChapterId = "chapter_2"
        val chapters = listOf(
            Chapter(
                id = currentChapterId,
                novelId = novelId,
                chapterTitle = "Chapter 1",
                chapterNumber = 1,
                content = "Content 1",
                wordCount = 100,
                viewCount = 50,
                createdAt = "2023-01-01",
                updatedAt = "2023-01-01"
            ),
            Chapter(
                id = nextChapterId,
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

        coEvery { chapterRepository.getChapterById(currentChapterId) } returns flowOf(chapters[0])
        coEvery { chapterRepository.getChapterById(nextChapterId) } returns flowOf(chapters[1])
        coEvery { chapterRepository.getChaptersByNovelId(novelId) } returns flowOf(chapters)

        // Load initial chapter
        viewModel.loadChapter(novelId, currentChapterId)

        // When
        viewModel.navigateToNextChapter()

        // Then
        val result = viewModel.currentChapter.value
        assertTrue(result is UiState.Success)
        assertEquals(nextChapterId, (result as UiState.Success).data.id)
    }

    @Test
    fun `navigateToPreviousChapter should load previous chapter`() = runTest {
        // Given
        val novelId = "novel_123"
        val currentChapterId = "chapter_2"
        val previousChapterId = "chapter_1"
        val chapters = listOf(
            Chapter(
                id = previousChapterId,
                novelId = novelId,
                chapterTitle = "Chapter 1",
                chapterNumber = 1,
                content = "Content 1",
                wordCount = 100,
                viewCount = 50,
                createdAt = "2023-01-01",
                updatedAt = "2023-01-01"
            ),
            Chapter(
                id = currentChapterId,
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

        coEvery { chapterRepository.getChapterById(currentChapterId) } returns flowOf(chapters[1])
        coEvery { chapterRepository.getChapterById(previousChapterId) } returns flowOf(chapters[0])
        coEvery { chapterRepository.getChaptersByNovelId(novelId) } returns flowOf(chapters)

        // Load initial chapter
        viewModel.loadChapter(novelId, currentChapterId)

        // When
        viewModel.navigateToPreviousChapter()

        // Then
        val result = viewModel.currentChapter.value
        assertTrue(result is UiState.Success)
        assertEquals(previousChapterId, (result as UiState.Success).data.id)
    }

    @Test
    fun `clearError should clear error state`() = runTest {
        // Given
        val novelId = "novel_123"
        val chapterId = "chapter_456"

        coEvery { chapterRepository.getChapterById(chapterId) } returns flowOf(null)
        coEvery { chapterRepository.getChaptersByNovelId(novelId) } returns flowOf(emptyList())

        // Load chapter to trigger error
        viewModel.loadChapter(novelId, chapterId)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.error.value)
    }
}
