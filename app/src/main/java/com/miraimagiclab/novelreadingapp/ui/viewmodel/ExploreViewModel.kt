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
class ExploreViewModel @Inject constructor(
    private val novelRepository: NovelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ExploreUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ExploreUiState>> = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                // Refresh data from API using parallel execution
                val refreshJobs = listOf(
                    async { novelRepository.refreshRecommendedNovels() },
                    async { novelRepository.refreshNewNovels() }
                )
                
                // Wait for all refresh operations to complete
                refreshJobs.awaitAll()

                // Combine all data streams
                combine(
                    novelRepository.getRecommendedNovels(),
                    novelRepository.getNewNovels()
                ) { recommendedNovels, newNovels ->
                    ExploreUiState(
                        recommendedNovels = recommendedNovels,
                        ourPicksNovels = newNovels
                    )
                }.collect { exploreUiState ->
                    _uiState.value = UiState.Success(exploreUiState)
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
}

data class ExploreUiState(
    val recommendedNovels: List<Novel> = emptyList(),
    val ourPicksNovels: List<Novel> = emptyList()
)


