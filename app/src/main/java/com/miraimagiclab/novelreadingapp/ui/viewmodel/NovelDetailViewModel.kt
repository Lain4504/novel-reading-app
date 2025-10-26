package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.auth.AuthState
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.domain.model.Chapter
import com.miraimagiclab.novelreadingapp.domain.model.NovelDetail
import com.miraimagiclab.novelreadingapp.domain.model.UserNovelInteraction
import com.miraimagiclab.novelreadingapp.domain.repository.NovelDetailRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelDetailViewModel @Inject constructor(
    private val novelDetailRepository: NovelDetailRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<NovelDetail>>(UiState.Idle)
    val uiState: StateFlow<UiState<NovelDetail>> = _uiState.asStateFlow()

    private val _userInteraction = MutableStateFlow<UserNovelInteraction?>(null)
    val userInteraction: StateFlow<UserNovelInteraction?> = _userInteraction.asStateFlow()

    private val _authState = sessionManager.authState
    val authState: StateFlow<AuthState> = _authState

    private var currentNovelId: String? = null

    fun loadNovelDetail(novelId: String) {
        currentNovelId = novelId
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                // Refresh data from API
                novelDetailRepository.refreshNovelDetail(novelId)
                
                // Load user interaction if user is logged in
                val authState = _authState.value
                if (authState.isLoggedIn && authState.userId != null) {
                    loadUserInteraction(authState.userId, novelId)
                }
                
                // Observe the cached data
                novelDetailRepository.getNovelDetail(novelId).collect { novelDetail ->
                    if (novelDetail != null) {
                        _uiState.value = UiState.Success(novelDetail)
                    } else {
                        _uiState.value = UiState.Error("Novel not found")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load novel details: ${e.message}")
            }
        }
    }

    private fun loadUserInteraction(userId: String, novelId: String) {
        viewModelScope.launch {
            try {
                novelDetailRepository.getUserInteraction(userId, novelId).collect { interaction ->
                    _userInteraction.value = interaction
                }
            } catch (e: Exception) {
                // Handle error silently - user interaction is optional
            }
        }
    }

    fun refreshData() {
        currentNovelId?.let { novelId ->
            loadNovelDetail(novelId)
        }
    }

    fun toggleFollow() {
        val authState = _authState.value
        val novelId = currentNovelId
        if (authState.isLoggedIn && authState.userId != null && novelId != null) {
            viewModelScope.launch {
                try {
                    val updatedInteraction = novelDetailRepository.toggleFollow(authState.userId, novelId)
                    _userInteraction.value = updatedInteraction
                } catch (e: Exception) {
                    // Handle error - could show snackbar
                }
            }
        }
    }

    fun toggleWishlist() {
        val authState = _authState.value
        val novelId = currentNovelId
        if (authState.isLoggedIn && authState.userId != null && novelId != null) {
            viewModelScope.launch {
                try {
                    val updatedInteraction = novelDetailRepository.toggleWishlist(authState.userId, novelId)
                    _userInteraction.value = updatedInteraction
                } catch (e: Exception) {
                    // Handle error - could show snackbar
                }
            }
        }
    }

    fun getLastReadChapter(): Chapter? {
        val interaction = _userInteraction.value
        val novelDetail = (_uiState.value as? UiState.Success)?.data
        return if (interaction?.currentChapterId != null && novelDetail != null) {
            novelDetail.chapters.find { it.id == interaction.currentChapterId }
        } else null
    }
}


