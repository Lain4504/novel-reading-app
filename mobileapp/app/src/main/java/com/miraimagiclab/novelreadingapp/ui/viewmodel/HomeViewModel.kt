package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.auth.AuthState
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.repository.NovelRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import com.miraimagiclab.novelreadingapp.util.RefreshManager
import com.miraimagiclab.novelreadingapp.util.RefreshType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val novelRepository: NovelRepository,
    sessionManager: SessionManager,
    private val refreshManager: RefreshManager
) : ViewModel() {
    val authState: StateFlow<AuthState> = sessionManager.authState

    private val _uiState = MutableStateFlow<UiState<HomeUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeUiState>> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // AI topic entered by user; when set, use topic-based AI recommendations
    private val _aiTopic = MutableStateFlow<String?>(null)
    val aiTopic: StateFlow<String?> = _aiTopic.asStateFlow()

    init {
        loadAllData()
        
        // Observe refresh events
        viewModelScope.launch {
            refreshManager.refreshEvent.collect { refreshType ->
                android.util.Log.d("HomeViewModel", "Received refresh event: $refreshType")
                if (refreshType == RefreshType.HOME || refreshType == RefreshType.ALL) {
                    android.util.Log.d("HomeViewModel", "Refreshing data...")
                    refreshData()
                }
            }
        }
    }

    private fun loadAllData() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                // Fetch fresh data from server
                loadDataFromServer()
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

    private suspend fun loadDataFromServer() {
        // Combine all data streams using new home screen specific methods
        val recFlow =
            if (_aiTopic.value?.isNotBlank() == true && authState.value.isLoggedIn) {
                // Topic-based AI recommendations using Gemini (user provided topic)
                novelRepository.getAiRecommendationsByTopic(_aiTopic.value!!.trim(), 12)
            } else if (authState.value.isLoggedIn && !authState.value.userId.isNullOrBlank()) {
                // Personalized recommendations using Gemini (based on interactions)
                novelRepository.getAiRecommendations(authState.value.userId!!, 12)
            } else {
                // Fallback to popular recommendations when not logged in
                novelRepository.getRecommendedNovels()
            }

        combine(
            novelRepository.getBannerNovels(),
            recFlow,
            novelRepository.getRankingNovels(),
            novelRepository.getNewNovels(),
            novelRepository.getCompletedNovels()
        ) { bannerNovels, recommendedNovels, rankingNovels, newNovels, completedNovels ->
            HomeUiState(
                bannerNovels = bannerNovels.take(5), // Top 5 for banner
                recommendedNovels = recommendedNovels,
                rankingNovels = rankingNovels,
                newNovels = newNovels,
                completedNovels = completedNovels
            )
        }.collect { homeUiState ->
            _uiState.value = UiState.Success(homeUiState)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            android.util.Log.d("HomeViewModel", "refreshData() called")
            _isRefreshing.value = true
            try {
                // Re-fetch fresh data from server
                loadDataFromServer()
                android.util.Log.d("HomeViewModel", "Data refreshed successfully")
            } catch (e: Exception) {
                // Handle error silently or update error state
                android.util.Log.e("HomeViewModel", "Error refreshing data: ${e.message}", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onBookClick(bookId: String) {
        // Handle book click - can be passed to UI via event
    }

    // Set or clear AI topic; triggers refresh to load topic-based recommendations
    fun setAiTopic(topic: String?) {
        _aiTopic.value = topic?.takeIf { it.isNotBlank() }
        refreshData()
    }
}

data class HomeUiState(
    val rankingNovels: List<Novel> = emptyList(),
    val recommendedNovels: List<Novel> = emptyList(),
    val bannerNovels: List<Novel> = emptyList(),
    val newNovels: List<Novel> = emptyList(),
    val completedNovels: List<Novel> = emptyList()
)
