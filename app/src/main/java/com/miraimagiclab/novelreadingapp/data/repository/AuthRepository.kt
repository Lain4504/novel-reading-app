package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.remote.api.AuthApiService
import com.miraimagiclab.novelreadingapp.data.remote.dto.*
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.util.NetworkResult
import com.miraimagiclab.novelreadingapp.util.NetworkResult.Loading
import com.miraimagiclab.novelreadingapp.util.JwtTokenHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) {

    fun login(request: LoginRequest): Flow<NetworkResult<LoginResponse>> = flow {
        try {
            emit(Loading)
            val response = authApiService.login(request)
            val result = handleApiResponse(response)
            if (result is NetworkResult.Success) {
                val login = result.data
                // Calculate expiration time for the token
                val expirationTime = try {
                    JwtTokenHelper.getExpirationTime(login.token).time
                } catch (e: Exception) {
                    null
                }
                // Persist session
                sessionManager.saveSession(
                    login.token,
                    login.refreshToken,
                    login.user.id,
                    login.user.username,
                    login.user.email,
                    null, // roles - will be updated later
                    expirationTime
                )
            }
            emit(result)
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun register(request: RegisterRequest): Flow<NetworkResult<UserDto>> = flow {
        try {
            emit(Loading)
            val response = authApiService.register(request)
            emit(handleApiResponse(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    fun forgotPassword(email: String): Flow<NetworkResult<Unit>> = flow {
        try {
            emit(Loading)
            val request = mapOf("email" to email)
            val response = authApiService.forgotPassword(request)
            emit(handleApiResponse(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun verifyResetOtp(email: String, code: String): Flow<NetworkResult<Boolean>> = flow {
        try {
            emit(Loading)
            val request = mapOf("email" to email, "code" to code)
            val response = authApiService.verifyResetOtp(request)
            emit(handleApiResponse(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun resetPassword(email: String, code: String, newPassword: String): Flow<NetworkResult<Unit>> = flow {
        try {
            emit(Loading)
            val request = mapOf("email" to email, "code" to code, "newPassword" to newPassword)
            val response = authApiService.resetPassword(request)
            emit(handleApiResponse(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

     fun verifyAccountOtp(email: String, code: String): Flow<NetworkResult<Boolean>> = flow {
        try {
            println("DEBUG: AuthRepository.verifyAccountOtp called with email: $email, code: $code")
            emit(Loading)
            val request = mapOf("email" to email, "code" to code)
            val response = authApiService.verifyAccountOtp(request)
            val result = handleApiResponse(response)
            println("DEBUG: AuthRepository.verifyAccountOtp result: $result")
            emit(result)
        } catch (e: Exception) {
            println("DEBUG: AuthRepository.verifyAccountOtp exception: ${e.message}")
            emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    private fun <T> handleApiResponse(response: Response<ApiResponse<T>>): NetworkResult<T> {
        return if (response.isSuccessful) {
            response.body()?.let { apiResponse ->
                if (apiResponse.success == true) {
                    NetworkResult.Success(apiResponse.data!!)
                } else {
                    NetworkResult.Error(apiResponse.message ?: "Unknown error")
                }
            } ?: NetworkResult.Error("Response body is null")
        } else {
            // Handle specific HTTP error codes
            when (response.code()) {
                400, 403, 409 -> {
                    // Try to parse error message from response body for auth and conflict errors
                    response.errorBody()?.string()?.let { errorBody ->
                        try {
                            // Parse JSON error response
                            val errorJson = com.google.gson.Gson().fromJson(errorBody, Map::class.java)
                            val message = errorJson["message"] as? String ?: "Request error"
                            NetworkResult.Error(message)
                        } catch (e: Exception) {
                            NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
                        }
                    } ?: NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
                }
                404 -> NetworkResult.Error("Unable to connect to server. Please check your internet connection.")
                else -> NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        }
    }
}