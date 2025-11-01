package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.repository.NovelRepository
import com.miraimagiclab.novelreadingapp.util.RefreshManager
import com.miraimagiclab.novelreadingapp.util.RefreshType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val novelRepository: NovelRepository,
    private val refreshManager: RefreshManager
) : ViewModel() {

    // Single state for all explore/search functionality
    private val _exploreState = MutableStateFlow(ExploreState())
    val exploreState: StateFlow<ExploreState> = _exploreState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadAllNovels()
        
        // Observe refresh events
        viewModelScope.launch {
            refreshManager.refreshEvent.collect { refreshType ->
                android.util.Log.d("ExploreViewModel", "Received refresh event: $refreshType")
                if (refreshType == RefreshType.EXPLORE || refreshType == RefreshType.ALL) {
                    android.util.Log.d("ExploreViewModel", "Refreshing data...")
                    refreshData()
                }
            }
        }
    }

    fun loadAllNovels() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                _exploreState.value = _exploreState.value.copy(isLoading = true, errorMessage = null)
                
                val currentState = _exploreState.value
                val response = novelRepository.searchNovels(
                    query = currentState.searchQuery,
                    page = 0,
                    size = 20,
                    sortBy = currentState.sortBy,
                    sortDirection = currentState.sortDirection
                )
                
                _exploreState.value = _exploreState.value.copy(
                    novels = response.content,
                    currentPage = 0,
                    hasMorePages = !response.last,
                    isLoading = false,
                    errorMessage = null
                )
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
                _exploreState.value = _exploreState.value.copy(
                    novels = emptyList(),
                    isLoading = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            android.util.Log.d("ExploreViewModel", "refreshData() called")
            _exploreState.value = _exploreState.value.copy(isRefreshing = true)
            try {
                val currentState = _exploreState.value
                val response = novelRepository.searchNovels(
                    query = currentState.searchQuery,
                    page = 0,
                    size = 20,
                    sortBy = currentState.sortBy,
                    sortDirection = currentState.sortDirection
                )
                
                _exploreState.value = _exploreState.value.copy(
                    novels = response.content,
                    currentPage = 0,
                    hasMorePages = !response.last,
                    isRefreshing = false,
                    errorMessage = null
                )
                android.util.Log.d("ExploreViewModel", "Data refreshed successfully")
            } catch (e: Exception) {
                android.util.Log.e("ExploreViewModel", "Error refreshing data: ${e.message}", e)
                _exploreState.value = _exploreState.value.copy(
                    isRefreshing = false,
                    errorMessage = "Failed to refresh data"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _exploreState.value = _exploreState.value.copy(searchQuery = query)
        
        // Perform search with debounce
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                _exploreState.value = _exploreState.value.copy(isLoading = true, errorMessage = null)
                
                // Debounce search by 300ms
                delay(300)
                
                val currentState = _exploreState.value
                android.util.Log.d("ExploreViewModel", "Searching for query: '$query'")
                val response = novelRepository.searchNovels(
                    query = query,
                    page = 0,
                    size = 20,
                    sortBy = currentState.sortBy,
                    sortDirection = currentState.sortDirection
                )
                android.util.Log.d("ExploreViewModel", "Search results: ${response.content.size} novels found")
                
                _exploreState.value = _exploreState.value.copy(
                    novels = response.content,
                    currentPage = 0,
                    hasMorePages = !response.last,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                android.util.Log.e("ExploreViewModel", "Search error: ${e.message}", e)
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "Unable to connect to the server. Please check your internet connection."
                    e.message?.contains("timeout") == true -> 
                        "Request timed out. Please try again."
                    e.message?.contains("404") == true -> 
                        "The requested content was not found."
                    e.message?.contains("500") == true -> 
                        "Server error. Please try again later."
                    else -> "Search failed: ${e.message ?: "Unknown error"}"
                }
                _exploreState.value = _exploreState.value.copy(
                    novels = emptyList(),
                    isLoading = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    fun loadMoreResults() {
        val currentState = _exploreState.value
        if (currentState.isLoadingMore || !currentState.hasMorePages) return
        
        viewModelScope.launch {
            try {
                _exploreState.value = _exploreState.value.copy(isLoadingMore = true)
                
                val nextPage = currentState.currentPage + 1
                val response = novelRepository.searchNovels(
                    query = currentState.searchQuery,
                    page = nextPage,
                    size = 20,
                    sortBy = currentState.sortBy,
                    sortDirection = currentState.sortDirection
                )
                
                _exploreState.value = _exploreState.value.copy(
                    novels = currentState.novels + response.content,
                    currentPage = nextPage,
                    hasMorePages = !response.last,
                    isLoadingMore = false
                )
            } catch (e: Exception) {
                _exploreState.value = _exploreState.value.copy(isLoadingMore = false)
            }
        }
    }

    fun updateSortOptions(sortBy: String, sortDirection: String) {
        _exploreState.value = _exploreState.value.copy(
            sortBy = sortBy,
            sortDirection = sortDirection
        )
        
        // Reload data with new sort options
        loadAllNovels()
    }

    fun clearSearch() {
        _exploreState.value = _exploreState.value.copy(searchQuery = "")
        loadAllNovels()
    }
}

/**
 * Unified state for Explore screen
 * Contains all novels (both explore and search results) in a single list
 */
data class ExploreState(
    val novels: List<Novel> = emptyList(),
    val searchQuery: String = "",
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val sortBy: String = "updatedAt",
    val sortDirection: String = "desc",
    val errorMessage: String? = null
)