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
                
                // Load chapter list first to ensure we have valid chapters
                if (allChapters.isEmpty() || currentNovelId != novelId) {
                    // Use sync method to avoid Flow transparency issues
                    val chapters = chapterRepository.getChaptersByNovelIdSync(novelId)
                    
                    if (chapters.isEmpty()) {
                        _currentChapter.value = UiState.Error("No chapters found")
                        return@launch
                    }
                    
                    allChapters = chapters
                    _chapterList.value = UiState.Success(chapters)
                    
                    // Verify chapterId exists in the list, if not use first chapter
                    val validChapterId = if (chapters.any { it.id == chapterId }) {
                        chapterId
                    } else {
                        chapters.firstOrNull()?.id ?: chapterId
                    }
                    
                    // Load the specific chapter with validated ID using sync method with novelId
                    // This will fetch from API if not in cache
                    var chapter = chapterRepository.getChapterByIdSync(novelId, validChapterId)
                    
                    // If chapter still not found, refresh chapter list to cache all chapters, then try again
                    if (chapter == null) {
                        try {
                            chapterRepository.refreshChaptersByNovelId(novelId)
                            val refreshedChapters = chapterRepository.getChaptersByNovelIdSync(novelId)
                            
                            if (refreshedChapters.isNotEmpty()) {
                                allChapters = refreshedChapters
                                _chapterList.value = UiState.Success(refreshedChapters)
                                
                                val finalValidChapterId = if (refreshedChapters.any { it.id == chapterId }) {
                                    chapterId
                                } else {
                                    refreshedChapters.firstOrNull()?.id ?: chapterId
                                }
                                
                                // Try to get chapter again after refresh
                                chapter = chapterRepository.getChapterByIdSync(novelId, finalValidChapterId)
                            }
                        } catch (e: Exception) {
                            // Ignore refresh errors, will show chapter not found below
                        }
                    }
                    
                                         if (chapter != null) {
                         currentChapterId = validChapterId
                         _currentChapter.value = UiState.Success(chapter)
                         // Track chapter view count (fire-and-forget)
                         chapterRepository.incrementViewCount(validChapterId)
                     } else {
                         _currentChapter.value = UiState.Error("Chapter not found")
                         _error.value = "Chapter not found"
                     }
                 } else {
                    // Chapter list already loaded, just load the specific chapter
                    val validChapterId = if (allChapters.any { it.id == chapterId }) {
                        chapterId
                    } else {
                        allChapters.firstOrNull()?.id ?: chapterId
                    }
                    
                    // Use method with novelId to allow fetching from API if not in cache
                    var chapter = chapterRepository.getChapterByIdSync(novelId, validChapterId)
                    
                    // If chapter not found, try refreshing chapter list and retry
                    if (chapter == null) {
                        try {
                            chapterRepository.refreshChaptersByNovelId(novelId)
                            val refreshedChapters = chapterRepository.getChaptersByNovelIdSync(novelId)
                            
                            if (refreshedChapters.isNotEmpty()) {
                                allChapters = refreshedChapters
                                _chapterList.value = UiState.Success(refreshedChapters)
                                
                                val finalValidChapterId = if (refreshedChapters.any { it.id == chapterId }) {
                                    chapterId
                                } else {
                                    refreshedChapters.firstOrNull()?.id ?: chapterId
                                }
                                
                                chapter = chapterRepository.getChapterByIdSync(novelId, finalValidChapterId)
                            }
                        } catch (e: Exception) {
                            // Ignore refresh errors, will show chapter not found below
                        }
                    }
                    
                    if (chapter != null) {
                        currentChapterId = validChapterId
                        _currentChapter.value = UiState.Success(chapter)
                        // Track chapter view count (fire-and-forget)
                        chapterRepository.incrementViewCount(validChapterId)
                    } else {
                        _currentChapter.value = UiState.Error("Chapter not found")
                    }
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
