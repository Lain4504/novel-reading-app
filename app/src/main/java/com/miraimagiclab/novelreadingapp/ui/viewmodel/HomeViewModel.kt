package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.repository.NovelRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val novelRepository: NovelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeUiState>> = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                // Refresh data from API using new home screen specific methods
                // Use parallel execution for better performance
                val refreshJobs = listOf(
                    async { novelRepository.refreshBannerNovels() },
                    async { novelRepository.refreshRecommendedNovels() },
                    async { novelRepository.refreshRankingNovels() },
                    async { novelRepository.refreshNewNovels() },
                    async { novelRepository.refreshCompletedNovels() }
                )
                
                // Wait for all refresh operations to complete
                refreshJobs.awaitAll()

                // Combine all data streams using new home screen specific methods
                combine(
                    novelRepository.getBannerNovels(),
                    novelRepository.getRecommendedNovels(),
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
        loadAllData()
    }

    fun onBookClick(bookId: String) {
        // Handle book click - can be passed to UI via event
    }
}

data class HomeUiState(
    val rankingNovels: List<Novel> = emptyList(),
    val recommendedNovels: List<Novel> = emptyList(),
    val bannerNovels: List<Novel> = emptyList(),
    val newNovels: List<Novel> = emptyList(),
    val completedNovels: List<Novel> = emptyList()
)
