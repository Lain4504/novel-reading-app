package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.repository.ReadingProgressRepository
import com.miraimagiclab.novelreadingapp.domain.model.ReadingProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingProgressViewModel @Inject constructor(
    private val readingProgressRepository: ReadingProgressRepository
) : ViewModel() {
    
    private val _currentProgress = MutableStateFlow<ReadingProgress?>(null)
    val currentProgress: StateFlow<ReadingProgress?> = _currentProgress.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun getReadingProgress(userId: String, novelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val progress = readingProgressRepository.getReadingProgress(userId, novelId)
                _currentProgress.value = progress
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load reading progress"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateReadingProgress(userId: String, novelId: String, chapterId: String, chapterNumber: Int) {
        viewModelScope.launch {
            println("DEBUG: ReadingProgressViewModel.updateReadingProgress called - userId: $userId, novelId: $novelId, chapterId: $chapterId, chapterNumber: $chapterNumber")
            _isLoading.value = true
            _error.value = null
            
            try {
                val updatedProgress = readingProgressRepository.updateReadingProgress(
                    userId = userId,
                    novelId = novelId,
                    chapterId = chapterId,
                    chapterNumber = chapterNumber
                )
                println("DEBUG: ReadingProgressViewModel.updateReadingProgress result: $updatedProgress")
                _currentProgress.value = updatedProgress
            } catch (e: Exception) {
                println("DEBUG: ReadingProgressViewModel.updateReadingProgress error: ${e.message}")
                e.printStackTrace()
                _error.value = e.message ?: "Failed to update reading progress"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
