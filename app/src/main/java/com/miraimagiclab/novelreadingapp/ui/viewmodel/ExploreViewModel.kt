package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.repository.NovelRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
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

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Novel>>(emptyList())
    val searchResults: StateFlow<List<Novel>> = _searchResults.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _sortBy = MutableStateFlow("updatedAt")
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    private val _sortDirection = MutableStateFlow("desc")
    val sortDirection: StateFlow<String> = _sortDirection.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                // Load all novels by default (empty search query)
                println("Loading all novels with sort: ${_sortBy.value}, direction: ${_sortDirection.value}")
                val response = novelRepository.searchNovels("", 0, 20, _sortBy.value, _sortDirection.value)
                println("Response received: ${response.content.size} novels")
                _searchResults.value = response.content
                _currentPage.value = 0
                _hasMorePages.value = !response.last
                
                _uiState.value = UiState.Success(ExploreUiState())
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

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            loadAllNovels()
        } else {
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                _isSearching.value = true
                _currentPage.value = 0
                _hasMorePages.value = true
                _searchResults.value = emptyList()
                
                // Debounce search by 300ms
                delay(300)
                
                val response = novelRepository.searchNovels(query, 0, 20, _sortBy.value, _sortDirection.value)
                _searchResults.value = response.content
                _currentPage.value = 0
                _hasMorePages.value = !response.last
            } catch (e: Exception) {
                // Handle error - could show error state
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun loadMoreResults() {
        if (_isLoadingMore.value || !_hasMorePages.value) return
        
        viewModelScope.launch {
            try {
                _isLoadingMore.value = true
                val nextPage = _currentPage.value + 1
                val response = novelRepository.searchNovels(_searchQuery.value, nextPage, 20, _sortBy.value, _sortDirection.value)
                
                _searchResults.value = _searchResults.value + response.content
                _currentPage.value = nextPage
                _hasMorePages.value = !response.last
            } catch (e: Exception) {
                // Handle error - could show error state
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    private fun loadAllNovels() {
        viewModelScope.launch {
            try {
                _isSearching.value = true
                _currentPage.value = 0
                _hasMorePages.value = true
                
                val response = novelRepository.searchNovels("", 0, 20, _sortBy.value, _sortDirection.value)
                _searchResults.value = response.content
                _currentPage.value = 0
                _hasMorePages.value = !response.last
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun updateSortOptions(sortBy: String, sortDirection: String) {
        _sortBy.value = sortBy
        _sortDirection.value = sortDirection
        // Reload data with new sort options
        if (_searchQuery.value.isBlank()) {
            loadAllNovels()
        } else {
            performSearch(_searchQuery.value)
        }
    }

    private fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _currentPage.value = 0
        _hasMorePages.value = true
        _isSearching.value = false
        _isLoadingMore.value = false
    }
}

data class ExploreUiState(
    val recommendedNovels: List<Novel> = emptyList(),
    val ourPicksNovels: List<Novel> = emptyList()
)


