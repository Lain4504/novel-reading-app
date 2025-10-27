package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.remote.dto.NovelDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserDto
import com.miraimagiclab.novelreadingapp.data.repository.AuthorRepository
import com.miraimagiclab.novelreadingapp.data.repository.UserRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authorRepository: AuthorRepository
) : ViewModel() {

    private val _userDetails = MutableStateFlow<UiState<UserDto>>(UiState.Loading)
    val userDetails: StateFlow<UiState<UserDto>> = _userDetails.asStateFlow()

    private val _authorNovels = MutableStateFlow<UiState<PageResponse<NovelDto>>>(UiState.Loading)
    val authorNovels: StateFlow<UiState<PageResponse<NovelDto>>> = _authorNovels.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadUserDetails(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _userDetails.value = UiState.Loading

            try {
                val result = userRepository.getUserById(userId)
                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    _userDetails.value = UiState.Success(user)
                    
                    // If user is an author, load their novels
                    if (user.roles.contains("AUTHOR")) {
                        loadAuthorNovels(userId)
                    } else {
                        _authorNovels.value = UiState.Success(PageResponse(emptyList(), 0, 0, 0L, 0, false, false, 0))
                    }
                } else {
                    _userDetails.value = UiState.Error(result.exceptionOrNull()?.message ?: "Failed to load user details")
                }
            } catch (e: Exception) {
                _userDetails.value = UiState.Error(e.message ?: "Failed to load user details")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadAuthorNovels(authorId: String) {
        viewModelScope.launch {
            _authorNovels.value = UiState.Loading
            
            try {
                val result = authorRepository.getNovelsByAuthor(authorId, page = 0, size = 20)
                if (result.isSuccess) {
                    _authorNovels.value = UiState.Success(result.getOrNull()!!)
                } else {
                    _authorNovels.value = UiState.Error(result.exceptionOrNull()?.message ?: "Failed to load author novels")
                }
            } catch (e: Exception) {
                _authorNovels.value = UiState.Error(e.message ?: "Failed to load author novels")
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun retry() {
        // Get the current user ID from the user details state
        val currentUser = (_userDetails.value as? UiState.Success)?.data
        if (currentUser != null) {
            loadUserDetails(currentUser.id)
        }
    }
}
