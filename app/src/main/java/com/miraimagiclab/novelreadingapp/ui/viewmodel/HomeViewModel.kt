package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.repository.NovelRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
                
                // Refresh data from API
                novelRepository.refreshTopNovelsByRating()
                novelRepository.refreshTopNovelsByFollowCount()
                novelRepository.refreshTopNovelsByViewCount()
                novelRepository.refreshRecentlyUpdatedNovels()
                novelRepository.refreshCompletedNovels()

                // Combine all data streams
                combine(
                    novelRepository.getTopNovelsByRating(),
                    novelRepository.getTopNovelsByFollowCount(),
                    novelRepository.getTopNovelsByViewCount(),
                    novelRepository.getRecentlyUpdatedNovels(),
                    novelRepository.getCompletedNovels()
                ) { rankingNovels, recommendedNovels, bannerNovels, newNovels, completedNovels ->
                    HomeUiState(
                        rankingNovels = rankingNovels,
                        recommendedNovels = recommendedNovels,
                        bannerNovels = bannerNovels.take(5), // Top 5 for banner
                        newNovels = newNovels,
                        completedNovels = completedNovels
                    )
                }.collect { homeUiState ->
                    _uiState.value = UiState.Success(homeUiState)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load data: ${e.message}")
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
