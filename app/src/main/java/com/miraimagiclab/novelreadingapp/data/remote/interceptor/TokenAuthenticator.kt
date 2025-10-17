package com.miraimagiclab.novelreadingapp.data.remote.interceptor

import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.data.remote.api.AuthApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Avoid infinite loop
        if (response.request.header("Authorization") != null && responseCount(response) >= 2) {
            return null
        }

        val newAccessToken = runBlocking {
            val currentRefresh = sessionManager.authState.value.refreshToken
            if (currentRefresh.isNullOrBlank()) return@runBlocking null

            try {
                val apiResponse = authApiService.refreshToken(currentRefresh)
                if (apiResponse.isSuccessful) {
                    val body = apiResponse.body()
                    if (body != null && body.success == true && body.data != null) {
                        val loginResponse = body.data
                        sessionManager.saveSession(
                            loginResponse.token,
                            loginResponse.refreshToken,
                            loginResponse.user.id,
                            loginResponse.user.username,
                            loginResponse.user.email
                        )
                        loginResponse.token
                    } else null
                } else null
            } catch (_: Exception) {
                null
            }
        }

        return newAccessToken?.let { token ->
            response.request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }
        return result
    }
}


