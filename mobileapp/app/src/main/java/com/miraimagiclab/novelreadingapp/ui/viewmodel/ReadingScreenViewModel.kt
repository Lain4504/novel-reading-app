package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.domain.model.Chapter
import com.miraimagiclab.novelreadingapp.domain.repository.ChapterRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingScreenViewModel @Inject constructor(
    private val chapterRepository: ChapterRepository
) : ViewModel() {

    private val _currentChapter = MutableStateFlow<UiState<Chapter>>(UiState.Loading)
    val currentChapter: StateFlow<UiState<Chapter>> = _currentChapter.asStateFlow()

    private val _chapterList = MutableStateFlow<UiState<List<Chapter>>>(UiState.Loading)
    val chapterList: StateFlow<UiState<List<Chapter>>> = _chapterList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentNovelId: String? = null
    private var currentChapterId: String? = null
    private var allChapters: List<Chapter> = emptyList()

    fun loadChapter(novelId: String, chapterId: String) {
        currentNovelId = novelId
        currentChapterId = chapterId
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // Load the specific chapter
                chapterRepository.getChapterById(chapterId).collect { chapter ->
                    if (chapter != null) {
                        _currentChapter.value = UiState.Success(chapter)
                        // Track chapter view count (fire-and-forget)
                        chapterRepository.incrementViewCount(chapterId)
                    } else {
                        _currentChapter.value = UiState.Error("Chapter not found")
                    }
                }
                
                // Load chapter list for navigation if not already loaded
                if (allChapters.isEmpty() || currentNovelId != novelId) {
                    loadChapterList(novelId)
                }
                
            } catch (e: Exception) {
                _currentChapter.value = UiState.Error("Failed to load chapter: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadChapterList(novelId: String) {
        currentNovelId = novelId
        
        viewModelScope.launch {
            try {
                chapterRepository.getChaptersByNovelId(novelId).collect { chapters ->
                    allChapters = chapters
                    _chapterList.value = UiState.Success(chapters)
                }
            } catch (e: Exception) {
                _chapterList.value = UiState.Error("Failed to load chapter list: ${e.message}")
                _error.value = e.message
            }
        }
    }

    fun getNextChapterId(): String? {
        val currentId = currentChapterId ?: return null
        val currentIndex = allChapters.indexOfFirst { it.id == currentId }
        return if (currentIndex >= 0 && currentIndex < allChapters.size - 1) {
            allChapters[currentIndex + 1].id
        } else null
    }

    fun getPreviousChapterId(): String? {
        val currentId = currentChapterId ?: return null
        val currentIndex = allChapters.indexOfFirst { it.id == currentId }
        return if (currentIndex > 0) {
            allChapters[currentIndex - 1].id
        } else null
    }

    fun hasNextChapter(): Boolean = getNextChapterId() != null

    fun hasPreviousChapter(): Boolean = getPreviousChapterId() != null

    fun navigateToChapter(chapterId: String) {
        val novelId = currentNovelId ?: return
        loadChapter(novelId, chapterId)
    }

    fun navigateToNextChapter() {
        val nextChapterId = getNextChapterId()
        if (nextChapterId != null) {
            navigateToChapter(nextChapterId)
        }
    }

    fun navigateToPreviousChapter() {
        val previousChapterId = getPreviousChapterId()
        if (previousChapterId != null) {
            navigateToChapter(previousChapterId)
        }
    }

    fun refreshCurrentChapter() {
        val chapterId = currentChapterId ?: return
        viewModelScope.launch {
            try {
                chapterRepository.refreshChapter(chapterId)
                loadChapter(currentNovelId ?: "", chapterId)
            } catch (e: Exception) {
                _error.value = "Failed to refresh chapter: ${e.message}"
            }
        }
    }

    fun refreshChapterList() {
        val novelId = currentNovelId ?: return
        viewModelScope.launch {
            try {
                chapterRepository.refreshChaptersByNovelId(novelId)
                loadChapterList(novelId)
            } catch (e: Exception) {
                _error.value = "Failed to refresh chapter list: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
