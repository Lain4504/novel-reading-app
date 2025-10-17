package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.BookDetail
import com.miraimagiclab.novelreadingapp.domain.repository.NovelDetailRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelDetailViewModel @Inject constructor(
    private val novelDetailRepository: NovelDetailRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<BookDetail>>(UiState.Idle)
    val uiState: StateFlow<UiState<BookDetail>> = _uiState.asStateFlow()

    private var currentNovelId: String? = null

    fun loadNovelDetail(novelId: String) {
        currentNovelId = novelId
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                // Refresh data from API
                novelDetailRepository.refreshNovelDetail(novelId)
                
                // Observe the cached data
                novelDetailRepository.getNovelDetail(novelId).collect { bookDetail ->
                    if (bookDetail != null) {
                        _uiState.value = UiState.Success(bookDetail)
                    } else {
                        _uiState.value = UiState.Error("Novel not found")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load novel details: ${e.message}")
            }
        }
    }

    fun refreshData() {
        currentNovelId?.let { novelId ->
            loadNovelDetail(novelId)
        }
    }
}


