package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.repository.UserNovelInteractionRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import com.miraimagiclab.novelreadingapp.util.RefreshManager
import com.miraimagiclab.novelreadingapp.util.RefreshType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val userNovelInteractionRepository: UserNovelInteractionRepository,
    private val sessionManager: SessionManager,
    private val refreshManager: RefreshManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<BookListUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<BookListUiState>> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        // Observe auth state and reset data when logged out
        viewModelScope.launch {
            sessionManager.authState.collect { state ->
                if (!state.isLoggedIn) {
                    // Reset to idle state when user logs out
                    _uiState.value = UiState.Idle
                } else if (_uiState.value is UiState.Idle || _uiState.value is UiState.Loading) {
                    // Load data when user is logged in and state is idle/loading
                    loadAllData()
                }
            }
        }
        
        // Observe refresh events
        viewModelScope.launch {
            refreshManager.refreshEvent.collect { refreshType ->
                if (refreshType == RefreshType.BOOKLIST || refreshType == RefreshType.ALL) {
                    refreshData()
                }
            }
        }
    }

    private fun loadAllData() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                val userId = sessionManager.authState.value.userId
                if (userId.isNullOrBlank()) {
                    _uiState.value = UiState.Error("User not logged in")
                    return@launch
                }
                
                println("DEBUG: BookListViewModel loading data for userId: $userId")
                
                // Get reading history (novels user has read - has reading progress)
                val readingHistoryNovels = userNovelInteractionRepository.getUserInProgressNovels(userId)
                println("DEBUG: BookListViewModel got ${readingHistoryNovels.size} reading history novels")
                
                // Get all novels that user is following
                val followingNovels = userNovelInteractionRepository.getUserFollowingNovels(userId)
                println("DEBUG: BookListViewModel got ${followingNovels.size} following novels")
                
                _uiState.value = UiState.Success(
                    BookListUiState(
                        novels = followingNovels,
                        readingHistoryNovels = readingHistoryNovels
                    )
                )
            } catch (e: Exception) {
                println("DEBUG: BookListViewModel error: ${e.message}")
                e.printStackTrace()
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "Unable to connect to the server. Please check your internet connection."
                    e.message?.contains("timeout") == true -> 
                        "Request timed out. Please try again."
                    e.message?.contains("404") == true -> 
                        "The requested content was not found."
                    e.message?.contains("500") == true -> 
                        "Server error. Please try again later."
                    else -> "Failed to load data: ${e.message ?: "Unknown error"}"
                }
                _uiState.value = UiState.Error(errorMessage)
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val userId = sessionManager.authState.value.userId
                if (userId.isNullOrBlank()) {
                    _isRefreshing.value = false
                    return@launch
                }
                
                // Re-fetch fresh data from server
                val readingHistoryNovels = userNovelInteractionRepository.getUserInProgressNovels(userId)
                val followingNovels = userNovelInteractionRepository.getUserFollowingNovels(userId)
                
                _uiState.value = UiState.Success(
                    BookListUiState(
                        novels = followingNovels,
                        readingHistoryNovels = readingHistoryNovels
                    )
                )
            } catch (e: Exception) {
                // Handle error silently or update error state
                println("Error refreshing data: ${e.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun deleteNovel(novelId: String) {
        viewModelScope.launch {
            try {
                val userId = sessionManager.authState.value.userId
                if (userId.isNullOrBlank()) {
                    return@launch
                }
                
                userNovelInteractionRepository.deleteUserNovelInteraction(userId, novelId)
                
                // Refresh the list after deletion
                loadAllData()
            } catch (e: Exception) {
                // Handle error silently or show error message
                e.printStackTrace()
            }
        }
    }
}

data class BookListUiState(
    val novels: List<Novel> = emptyList(),
    val readingHistoryNovels: List<Novel> = emptyList()
)


