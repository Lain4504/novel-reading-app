package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.data.auth.AuthState
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserUpdateRequest
import com.miraimagiclab.novelreadingapp.data.repository.ImageRepository
import com.miraimagiclab.novelreadingapp.data.repository.UserRepository
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    val authState: StateFlow<AuthState> = sessionManager.authState

    private val _userDetails = MutableStateFlow<UiState<UserDto>>(UiState.Idle)
    val userDetails: StateFlow<UiState<UserDto>> = _userDetails

    private val _updateUserState = MutableStateFlow<UiState<UserDto>>(UiState.Idle)
    val updateUserState: StateFlow<UiState<UserDto>> = _updateUserState

    private val _uploadImageState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uploadImageState: StateFlow<UiState<String>> = _uploadImageState

    init {
        // Observe auth state and reset user details when logged out
        viewModelScope.launch {
            authState.collect { state ->
                if (!state.isLoggedIn) {
                    _userDetails.value = UiState.Idle
                    _updateUserState.value = UiState.Idle
                    _uploadImageState.value = UiState.Idle
                }
            }
        }
    }

    fun loadUserDetails() {
        val userId = authState.value.userId ?: return
        viewModelScope.launch {
            _userDetails.value = UiState.Loading
            val result = userRepository.getUserById(userId)
            _userDetails.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Failed to load user details")
            }
        }
    }

    fun uploadImage(imageFile: File) {
        val userId = authState.value.userId ?: return
        viewModelScope.launch {
            _uploadImageState.value = UiState.Loading
            val result = imageRepository.uploadImage(imageFile, userId, "USER")
            _uploadImageState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!.url)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Failed to upload image")
            }
        }
    }

    fun updateUserProfile(
        avatarUrl: String?,
        backgroundUrl: String?,
        bio: String?,
        displayName: String?,
        avatarFile: File?,
        backgroundFile: File?
    ) {
        val userId = authState.value.userId ?: return
        viewModelScope.launch {
            try {
                _updateUserState.value = UiState.Loading
                
                var finalAvatarUrl = avatarUrl
                var finalBackgroundUrl = backgroundUrl
                
                // Upload avatar image if provided
                if (avatarFile != null) {
                    val avatarResult = imageRepository.uploadImage(avatarFile, userId, "USER")
                    if (avatarResult.isSuccess) {
                        finalAvatarUrl = avatarResult.getOrNull()!!.url
                    } else {
                        _updateUserState.value = UiState.Error("Failed to upload avatar: ${avatarResult.exceptionOrNull()?.message}")
                        return@launch
                    }
                }
                
                // Upload background image if provided
                if (backgroundFile != null) {
                    val backgroundResult = imageRepository.uploadImage(backgroundFile, userId, "USER")
                    if (backgroundResult.isSuccess) {
                        finalBackgroundUrl = backgroundResult.getOrNull()!!.url
                    } else {
                        _updateUserState.value = UiState.Error("Failed to upload background: ${backgroundResult.exceptionOrNull()?.message}")
                        return@launch
                    }
                }
                
                // Update user profile with final URLs
                val request = UserUpdateRequest(
                    avatarUrl = finalAvatarUrl,
                    backgroundUrl = finalBackgroundUrl,
                    bio = bio,
                    displayName = displayName
                )
                val result = userRepository.updateUser(userId, request)
                _updateUserState.value = if (result.isSuccess) {
                    UiState.Success(result.getOrNull()!!)
                } else {
                    UiState.Error(result.exceptionOrNull()?.message ?: "Failed to update user")
                }
            } catch (e: Exception) {
                _updateUserState.value = UiState.Error("Failed to update profile: ${e.message}")
            }
        }
    }

    fun resetUpdateState() {
        _updateUserState.value = UiState.Idle
    }

    fun resetUploadImageState() {
        _uploadImageState.value = UiState.Idle
    }
}


