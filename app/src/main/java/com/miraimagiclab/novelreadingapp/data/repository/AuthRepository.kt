package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.remote.api.UserApiService
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
    private val userApiService: UserApiService,
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
                // Extract roles from JWT token
                val roles = try {
                    JwtTokenHelper.getRoles(login.token)
                } catch (e: Exception) {
                    emptySet()
                }
                // Persist session
                sessionManager.saveSession(
                    login.token,
                    login.refreshToken,
                    login.user.id,
                    login.user.username,
                    login.user.email,
                    roles,
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

   fun resendVerification(email: String): Flow<NetworkResult<Unit>> = flow {
       try {
           emit(Loading)
           val response = authApiService.resendVerification(email)
           emit(handleApiResponseForUnit(response))
       } catch (e: Exception) {
           emit(NetworkResult.Error(e.message ?: "Unknown error occurred"))
       }
   }

   fun changePassword(userId: String, currentPassword: String, newPassword: String, confirmPassword: String): Flow<NetworkResult<Unit>> = flow {
       try {
           emit(Loading)
           val request = mapOf(
               "currentPassword" to currentPassword,
               "newPassword" to newPassword,
               "confirmPassword" to confirmPassword
           )
           val response = userApiService.changePassword(userId, request)
           emit(handleApiResponseForUnit(response))
       } catch (e: Exception) {
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
                400 -> {
                    // Try to parse error message from response body for validation errors
                    response.errorBody()?.string()?.let { errorBody ->
                        try {
                            // Parse JSON error response - the backend returns {"success":false,"message":"...","timestamp":"..."}
                            val errorJson = com.google.gson.Gson().fromJson(errorBody, Map::class.java)
                            val message = errorJson["message"] as? String ?: "Request error"
                            NetworkResult.Error(message)
                        } catch (e: Exception) {
                            NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
                        }
                    } ?: NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
                }
                401 -> NetworkResult.Error("Unauthorized: Please log in again.")
                403 -> NetworkResult.Error("Forbidden: You don't have permission to perform this action.")
                409 -> NetworkResult.Error("Conflict: The request conflicts with the current state.")
                404 -> NetworkResult.Error("Unable to connect to server. Please check your internet connection.")
                else -> NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        }
    }

    private fun handleApiResponseForUnit(response: Response<ApiResponse<Unit>>): NetworkResult<Unit> {
        return if (response.isSuccessful) {
            response.body()?.let { apiResponse ->
                if (apiResponse.success == true) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(apiResponse.message ?: "Unknown error")
                }
            } ?: NetworkResult.Error("Response body is null")
        } else {
            // Handle specific HTTP error codes
            when (response.code()) {
                400 -> {
                    // Try to parse error message from response body for validation errors
                    response.errorBody()?.string()?.let { errorBody ->
                        try {
                            // Parse JSON error response - the backend returns {"success":false,"message":"...","timestamp":"..."}
                            val errorJson = com.google.gson.Gson().fromJson(errorBody, Map::class.java)
                            val message = errorJson["message"] as? String ?: "Request error"
                            NetworkResult.Error(message)
                        } catch (e: Exception) {
                            NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
                        }
                    } ?: NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
                }
                401 -> NetworkResult.Error("Unauthorized: Please log in again.")
                403 -> NetworkResult.Error("Forbidden: You don't have permission to perform this action.")
                409 -> NetworkResult.Error("Conflict: The request conflicts with the current state.")
                404 -> NetworkResult.Error("Unable to connect to server. Please check your internet connection.")
                else -> NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        }
    }
}