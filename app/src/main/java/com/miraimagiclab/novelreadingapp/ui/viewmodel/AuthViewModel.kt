package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.remote.dto.*
import com.miraimagiclab.novelreadingapp.data.repository.AuthRepository
import com.miraimagiclab.novelreadingapp.util.NetworkResult
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<LoginResponse>> = _loginState

    private val _registerState = MutableStateFlow<UiState<UserDto>>(UiState.Idle)
    val registerState: StateFlow<UiState<UserDto>> = _registerState

    private val _forgotPasswordState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val forgotPasswordState: StateFlow<UiState<Unit>> = _forgotPasswordState

    private val _verifyOtpState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val verifyOtpState: StateFlow<UiState<Boolean>> = _verifyOtpState
    
    private val _verifyAccountState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val verifyAccountState: StateFlow<UiState<Boolean>> = _verifyAccountState

    private val _resetPasswordState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val resetPasswordState: StateFlow<UiState<Unit>> = _resetPasswordState

    fun login(usernameOrEmail: String, password: String) {
        viewModelScope.launch {
            authRepository.login(LoginRequest(usernameOrEmail, password)).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _loginState.value = UiState.Loading
                    is NetworkResult.Success -> _loginState.value = UiState.Success(result.data)
                    is NetworkResult.Error -> _loginState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.register(RegisterRequest(username, email, password)).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _registerState.value = UiState.Loading
                    is NetworkResult.Success -> _registerState.value = UiState.Success(result.data)
                    is NetworkResult.Error -> _registerState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            authRepository.forgotPassword(email).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _forgotPasswordState.value = UiState.Loading
                    is NetworkResult.Success -> _forgotPasswordState.value = UiState.Success(result.data)
                    is NetworkResult.Error -> _forgotPasswordState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun verifyResetOtp(email: String, code: String) {
        viewModelScope.launch {
            authRepository.verifyResetOtp(email, code).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _verifyOtpState.value = UiState.Loading
                    is NetworkResult.Success -> _verifyOtpState.value = UiState.Success(result.data)
                    is NetworkResult.Error -> _verifyOtpState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun resetPassword(email: String, code: String, newPassword: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email, code, newPassword).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _resetPasswordState.value = UiState.Loading
                    is NetworkResult.Success -> _resetPasswordState.value = UiState.Success(result.data)
                    is NetworkResult.Error -> _resetPasswordState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = UiState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = UiState.Idle
    }

    fun resetForgotPasswordState() {
        _forgotPasswordState.value = UiState.Idle
    }

    fun resetVerifyOtpState() {
        _verifyOtpState.value = UiState.Idle
    }

     fun verifyAccountOtp(email: String, code: String) {
        println("DEBUG: AuthViewModel.verifyAccountOtp called with email: $email, code: $code")
        viewModelScope.launch {
            authRepository.verifyAccountOtp(email, code).collect { result ->
                println("DEBUG: AuthViewModel.verifyAccountOtp result: $result")
                when (result) {
                    is NetworkResult.Loading -> _verifyAccountState.value = UiState.Loading
                    is NetworkResult.Success -> {
                        println("DEBUG: AuthViewModel.verifyAccountOtp success: ${result.data}")
                        _verifyAccountState.value = UiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        println("DEBUG: AuthViewModel.verifyAccountOtp error: ${result.message}")
                        _verifyAccountState.value = UiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun resetResetPasswordState() {
        _resetPasswordState.value = UiState.Idle
    }

    fun resetVerifyAccountState() {
        _verifyAccountState.value = UiState.Idle
    }
}