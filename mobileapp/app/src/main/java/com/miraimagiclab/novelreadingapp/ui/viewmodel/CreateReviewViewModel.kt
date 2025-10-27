package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.remote.api.ReviewApiService
import com.miraimagiclab.novelreadingapp.ui.components.novel.ReviewRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateReviewViewModel @Inject constructor(
    private val reviewApiService: ReviewApiService,
    private val sessionManager: com.miraimagiclab.novelreadingapp.data.auth.SessionManager,
    private val readingProgressRepository: com.miraimagiclab.novelreadingapp.data.repository.ReadingProgressRepository,
    private val novelApiService: com.miraimagiclab.novelreadingapp.data.remote.api.NovelApiService
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()
    
    private val _submitResult = MutableStateFlow<com.miraimagiclab.novelreadingapp.util.UiState<Boolean>>(com.miraimagiclab.novelreadingapp.util.UiState.Idle)
    val submitResult: StateFlow<com.miraimagiclab.novelreadingapp.util.UiState<Boolean>> = _submitResult.asStateFlow()
    
    private val _novelInfo = MutableStateFlow<com.miraimagiclab.novelreadingapp.util.UiState<Pair<Int, Int>>>(com.miraimagiclab.novelreadingapp.util.UiState.Idle)
    val novelInfo: StateFlow<com.miraimagiclab.novelreadingapp.util.UiState<Pair<Int, Int>>> = _novelInfo.asStateFlow()
    
    fun loadNovelInfo(novelId: String) {
        viewModelScope.launch {
            try {
                _novelInfo.value = com.miraimagiclab.novelreadingapp.util.UiState.Loading
                
                val currentUser = sessionManager.authState.value
                if (!currentUser.isLoggedIn || currentUser.userId == null) {
                    _novelInfo.value = com.miraimagiclab.novelreadingapp.util.UiState.Error("User not logged in")
                    return@launch
                }
                
                // Get user's reading progress for this novel
                val readingProgress = readingProgressRepository.getReadingProgress(currentUser.userId, novelId)
                val chaptersRead = readingProgress?.currentChapterNumber ?: 0
                
                // Get novel detail to get total chapters
                val novelResponse = novelApiService.getNovelById(novelId)
                val totalChapters = if (novelResponse.success && novelResponse.data != null) {
                    novelResponse.data!!.chapterCount ?: 1
                } else {
                    1 // Fallback to 1 if we can't get novel info
                }
                
                _novelInfo.value = com.miraimagiclab.novelreadingapp.util.UiState.Success(Pair(chaptersRead, totalChapters))
                
            } catch (e: Exception) {
                _novelInfo.value = com.miraimagiclab.novelreadingapp.util.UiState.Error("Failed to load novel info: ${e.message}")
            }
        }
    }

    fun submitReview(novelId: String, review: ReviewRequest) {
        viewModelScope.launch {
            try {
                _isSubmitting.value = true
                _submitResult.value = com.miraimagiclab.novelreadingapp.util.UiState.Loading
                
                val currentUser = sessionManager.authState.value
                if (!currentUser.isLoggedIn || currentUser.userId == null) {
                    _submitResult.value = com.miraimagiclab.novelreadingapp.util.UiState.Error("User not logged in")
                    return@launch
                }
                
                // Use the already loaded novel info if available, otherwise load it
                val (chaptersRead, totalChapters) = if (_novelInfo.value is com.miraimagiclab.novelreadingapp.util.UiState.Success) {
                    val (chapters, total) = (_novelInfo.value as com.miraimagiclab.novelreadingapp.util.UiState.Success<Pair<Int, Int>>).data
                    // Ensure totalChapters is at least 1 (backend validation requirement)
                    Pair(chapters, maxOf(total, 1))
                } else {
                    // Fallback: load the info again
                    val readingProgress = readingProgressRepository.getReadingProgress(currentUser.userId, novelId)
                    val chaptersRead = readingProgress?.currentChapterNumber ?: 0
                    
                    val novelResponse = novelApiService.getNovelById(novelId)
                    val totalChapters = if (novelResponse.success && novelResponse.data != null) {
                        // Ensure totalChapters is at least 1 (backend validation requirement)
                        maxOf(novelResponse.data!!.chapterCount ?: 1, 1)
                    } else {
                        1 // Fallback to 1 if we can't get novel info
                    }
                    
                    Pair(chaptersRead, totalChapters)
                }
                
                // Log for debugging
                println("Review creation - User: ${currentUser.userId}, Novel: $novelId")
                println("Reading progress - Chapters read: $chaptersRead, Total chapters: $totalChapters")
                
                val request = com.miraimagiclab.novelreadingapp.data.remote.dto.request.ReviewCreateRequestDto(
                    userId = currentUser.userId,
                    novelId = novelId,
                    writingQuality = review.writingQuality,
                    stabilityOfUpdates = review.stabilityOfUpdates,
                    storyDevelopment = review.storyDevelopment,
                    characterDesign = review.characterDesign,
                    worldBackground = review.worldBackground,
                    reviewText = review.reviewText,
                    chaptersReadWhenReviewed = chaptersRead,
                    totalChaptersAtReview = totalChapters
                )
                
                val response = reviewApiService.createReview(request)
                if (response.success && response.data != null) {
                    _submitResult.value = com.miraimagiclab.novelreadingapp.util.UiState.Success(true)
                } else {
                    val errorMessage = response.message ?: "Unknown error occurred"
                    _submitResult.value = com.miraimagiclab.novelreadingapp.util.UiState.Error("Failed to create review: $errorMessage")
                }
                
            } catch (e: retrofit2.HttpException) {
                // Handle HTTP errors specifically
                val errorMessage = when (e.code()) {
                    401 -> "Unauthorized: Please log in to submit a review"
                    403 -> "Forbidden: You may not have permission to review this novel, or your session has expired"
                    400 -> "Bad Request: Please check your review content and try again"
                    500 -> "Server Error: Please try again later"
                    else -> "HTTP ${e.code()}: ${e.message()}"
                }
                _submitResult.value = com.miraimagiclab.novelreadingapp.util.UiState.Error(errorMessage)
            } catch (e: java.net.UnknownHostException) {
                _submitResult.value = com.miraimagiclab.novelreadingapp.util.UiState.Error("Network Error: Please check your internet connection")
            } catch (e: java.net.SocketTimeoutException) {
                _submitResult.value = com.miraimagiclab.novelreadingapp.util.UiState.Error("Request Timeout: Please try again")
            } catch (e: Exception) {
                _submitResult.value = com.miraimagiclab.novelreadingapp.util.UiState.Error("Failed to create review: ${e.message ?: "Unknown error occurred"}")
            } finally {
                _isSubmitting.value = false
            }
        }
    }
    
    fun clearSubmitResult() {
        _submitResult.value = com.miraimagiclab.novelreadingapp.util.UiState.Idle
    }
}
