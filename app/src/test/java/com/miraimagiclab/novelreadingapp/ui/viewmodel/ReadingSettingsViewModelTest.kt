package com.miraimagiclab.novelreadingapp.ui.viewmodel

import com.miraimagiclab.novelreadingapp.data.repository.ReadingSettingsRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReadingSettingsViewModelTest {

    private lateinit var viewModel: ReadingSettingsViewModel
    private lateinit var mockRepository: ReadingSettingsRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        viewModel = ReadingSettingsViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setFontFamily should update repository`() = runTest {
        // Given
        val fontFamily = "literata"
        coEvery { mockRepository.setFontFamily(fontFamily) } just Runs

        // When
        viewModel.setFontFamily(fontFamily)

        // Then
        coVerify { mockRepository.setFontFamily(fontFamily) }
    }

    @Test
    fun `setFontSize should update repository`() = runTest {
        // Given
        val fontSize = 18f
        coEvery { mockRepository.setFontSize(fontSize) } just Runs

        // When
        viewModel.setFontSize(fontSize)

        // Then
        coVerify { mockRepository.setFontSize(fontSize) }
    }

    @Test
    fun `setLineSpacing should update repository`() = runTest {
        // Given
        val lineSpacing = 1.8f
        coEvery { mockRepository.setLineSpacing(lineSpacing) } just Runs

        // When
        viewModel.setLineSpacing(lineSpacing)

        // Then
        coVerify { mockRepository.setLineSpacing(lineSpacing) }
    }

    @Test
    fun `setReadingTheme should update repository`() = runTest {
        // Given
        val theme = "dark"
        coEvery { mockRepository.setReadingTheme(theme) } just Runs

        // When
        viewModel.setReadingTheme(theme)

        // Then
        coVerify { mockRepository.setReadingTheme(theme) }
    }

    @Test
    fun `fontFamily flow should emit repository values`() = runTest {
        // Given
        val expectedFontFamily = "nunito"
        every { mockRepository.getFontFamilyFlow() } returns flowOf(expectedFontFamily)

        // When
        val fontFamily = viewModel.fontFamily

        // Then
        assert(fontFamily.value == expectedFontFamily)
    }

    @Test
    fun `fontSize flow should emit repository values`() = runTest {
        // Given
        val expectedFontSize = 16f
        every { mockRepository.getFontSizeFlow() } returns flowOf(expectedFontSize)

        // When
        val fontSize = viewModel.fontSize

        // Then
        assert(fontSize.value == expectedFontSize)
    }

    @Test
    fun `lineSpacing flow should emit repository values`() = runTest {
        // Given
        val expectedLineSpacing = 1.5f
        every { mockRepository.getLineSpacingFlow() } returns flowOf(expectedLineSpacing)

        // When
        val lineSpacing = viewModel.lineSpacing

        // Then
        assert(lineSpacing.value == expectedLineSpacing)
    }

    @Test
    fun `readingTheme flow should emit repository values`() = runTest {
        // Given
        val expectedTheme = "sepia"
        every { mockRepository.getReadingThemeFlow() } returns flowOf(expectedTheme)

        // When
        val readingTheme = viewModel.readingTheme

        // Then
        assert(readingTheme.value == expectedTheme)
    }
}
