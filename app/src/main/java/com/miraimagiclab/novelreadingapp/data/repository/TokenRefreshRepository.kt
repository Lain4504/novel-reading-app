package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.remote.api.AuthApiService
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.util.JwtTokenHelper
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

sealed class TokenRefreshResult {
    object Success : TokenRefreshResult()
    object NoRefreshNeeded : TokenRefreshResult()
    object RefreshTokenExpired : TokenRefreshResult()
    data class Error(val message: String) : TokenRefreshResult()
}

@Singleton
class TokenRefreshRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) {
    
    /**
     * Checks if token needs refresh and refreshes if necessary
     * @param currentToken Current access token
     * @param refreshToken Current refresh token
     * @param thresholdMinutes Minutes before expiration to consider "expiring soon"
     * @return TokenRefreshResult indicating the outcome
     */
    suspend fun refreshTokenIfNeeded(
        currentToken: String?, 
        refreshToken: String?, 
        thresholdMinutes: Int = 60
    ): TokenRefreshResult {
        // If no tokens exist, no refresh needed
        if (currentToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            return TokenRefreshResult.NoRefreshNeeded
        }
        
        // Check if current token is expired or expiring soon
        val needsRefresh = try {
            JwtTokenHelper.isTokenExpired(currentToken) || 
            JwtTokenHelper.isTokenExpiringSoon(currentToken, thresholdMinutes)
        } catch (e: Exception) {
            // If we can't parse the token, consider it needs refresh
            true
        }
        
        if (!needsRefresh) {
            return TokenRefreshResult.NoRefreshNeeded
        }
        
        // Attempt to refresh the token
        return try {
            val response = authApiService.refreshToken(refreshToken)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success == true && body.data != null) {
                    val loginResponse = body.data
                    
                    // Calculate expiration time for the new token
                    val expirationTime = try {
                        JwtTokenHelper.getExpirationTime(loginResponse.token).time
                    } catch (e: Exception) {
                        null
                    }
                    
                    // Save the new session
                    sessionManager.saveSession(
                        loginResponse.token,
                        loginResponse.refreshToken,
                        loginResponse.user.id,
                        loginResponse.user.username,
                        loginResponse.user.email,
                        expirationTime
                    )
                    
                    TokenRefreshResult.Success
                } else {
                    TokenRefreshResult.Error("Invalid response from server")
                }
            } else {
                // If refresh fails with 401/403, refresh token is expired
                if (response.code() == 401 || response.code() == 403) {
                    sessionManager.clearSession()
                    TokenRefreshResult.RefreshTokenExpired
                } else {
                    TokenRefreshResult.Error("Failed to refresh token: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            TokenRefreshResult.Error("Network error: ${e.message}")
        }
    }
    
    /**
     * Validates current session and refreshes token if needed
     * This is a convenience method that gets tokens from SessionManager
     */
    suspend fun validateAndRefreshCurrentSession(thresholdMinutes: Int = 60): TokenRefreshResult {
        val authState = sessionManager.authState.first()
        return refreshTokenIfNeeded(
            authState.accessToken,
            authState.refreshToken,
            thresholdMinutes
        )
    }
}
