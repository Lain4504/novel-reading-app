package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.repository.UserNovelInteractionRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val userNovelInteractionRepository: UserNovelInteractionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<BookListUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<BookListUiState>> = _uiState.asStateFlow()

    init {
        loadFollowingNovels()
    }

    private fun loadFollowingNovels() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                val userId = sessionManager.authState.value.userId
                if (userId.isNullOrBlank()) {
                    _uiState.value = UiState.Error("User not logged in")
                    return@launch
                }
                
                // Get all novels that user is following
                val followingNovels = userNovelInteractionRepository.getUserFollowingNovels(userId)
                
                _uiState.value = UiState.Success(BookListUiState(novels = followingNovels))
            } catch (e: Exception) {
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
        loadFollowingNovels()
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
                loadFollowingNovels()
            } catch (e: Exception) {
                // Handle error silently or show error message
                e.printStackTrace()
            }
        }
    }
}

data class BookListUiState(
    val novels: List<Novel> = emptyList()
)


