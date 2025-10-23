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
class InProgressViewModel @Inject constructor(
    private val userNovelInteractionRepository: UserNovelInteractionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<InProgressUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<InProgressUiState>> = _uiState.asStateFlow()

    init {
        // Observe auth state and reset data when logged out
        viewModelScope.launch {
            sessionManager.authState.collect { state ->
                if (!state.isLoggedIn) {
                    // Reset to idle state when user logs out
                    _uiState.value = UiState.Idle
                } else if (_uiState.value is UiState.Idle || _uiState.value is UiState.Loading) {
                    // Load data when user is logged in and state is idle/loading
                    loadInProgressNovels()
                }
            }
        }
    }

    private fun loadInProgressNovels() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                val userId = sessionManager.authState.value.userId
                if (userId.isNullOrBlank()) {
                    _uiState.value = UiState.Error("User not logged in")
                    return@launch
                }
                
                // Get all novels that user is currently reading
                val inProgressNovels = userNovelInteractionRepository.getUserInProgressNovels(userId)
                
                _uiState.value = UiState.Success(InProgressUiState(novels = inProgressNovels))
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
        loadInProgressNovels()
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
                loadInProgressNovels()
            } catch (e: Exception) {
                // Handle error silently or show error message
                e.printStackTrace()
            }
        }
    }
}

data class InProgressUiState(
    val novels: List<Novel> = emptyList()
)


