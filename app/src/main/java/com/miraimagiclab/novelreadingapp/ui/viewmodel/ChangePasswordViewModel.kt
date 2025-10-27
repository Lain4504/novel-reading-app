package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.data.repository.AuthRepository
import com.miraimagiclab.novelreadingapp.util.NetworkResult
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _changePasswordState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val changePasswordState: StateFlow<UiState<Unit>> = _changePasswordState.asStateFlow()

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        // Validate inputs
        when {
            currentPassword.isBlank() -> {
                _changePasswordState.value = UiState.Error("Current password is required")
                return
            }
            newPassword.isBlank() -> {
                _changePasswordState.value = UiState.Error("New password is required")
                return
            }
            confirmPassword.isBlank() -> {
                _changePasswordState.value = UiState.Error("Please confirm your new password")
                return
            }
            newPassword != confirmPassword -> {
                _changePasswordState.value = UiState.Error("New password and confirmation do not match")
                return
            }
            newPassword.length < 8 -> {
                _changePasswordState.value = UiState.Error("New password must be at least 8 characters long")
                return
            }
            !newPassword.matches(Regex(".*[a-z].*")) -> {
                _changePasswordState.value = UiState.Error("New password must contain at least one lowercase letter")
                return
            }
            !newPassword.matches(Regex(".*[A-Z].*")) -> {
                _changePasswordState.value = UiState.Error("New password must contain at least one uppercase letter")
                return
            }
            !newPassword.matches(Regex(".*\\d.*")) -> {
                _changePasswordState.value = UiState.Error("New password must contain at least one number")
                return
            }
        }

        val userId = sessionManager.authState.value.userId ?: run {
            println("DEBUG: ChangePasswordViewModel - userId is null, authState: ${sessionManager.authState.value}")
            _changePasswordState.value = UiState.Error("User not authenticated")
            return
        }

        println("DEBUG: ChangePasswordViewModel - proceeding with userId: $userId")

        viewModelScope.launch {
            authRepository.changePassword(userId, currentPassword, newPassword, confirmPassword)
                .collect { result ->
                    _changePasswordState.value = when (result) {
                        is NetworkResult.Loading -> UiState.Loading
                        is NetworkResult.Success -> UiState.Success(Unit)
                        is NetworkResult.Error -> UiState.Error(result.message ?: "Unknown error occurred")
                        else -> UiState.Error("Unknown error occurred")
                    }
                }
        }
    }

    fun resetState() {
        _changePasswordState.value = UiState.Idle
    }
}