package com.miraimagiclab.novelreadingapp.data.remote.interceptor

import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = runBlocking { sessionManager.authState.value.accessToken }
        return if (!token.isNullOrBlank()) {
            val newReq = original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newReq)
        } else {
            chain.proceed(original)
        }
    }
}


