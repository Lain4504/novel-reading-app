package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.LoginRequest
import com.miraimagiclab.novelreadingapp.data.remote.dto.RegisterRequest
import com.miraimagiclab.novelreadingapp.data.remote.dto.LoginResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {

    @POST("users")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<UserDto>>

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: Map<String, String>): Response<ApiResponse<Unit>>

    @POST("auth/verify-reset-otp")
    suspend fun verifyResetOtp(@Body request: Map<String, String>): Response<ApiResponse<Boolean>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: Map<String, String>): Response<ApiResponse<Unit>>

    @POST("users/refresh")
    suspend fun refreshToken(@Query("refreshToken") refreshToken: String): Response<ApiResponse<LoginResponse>>
    
    @POST("auth/verify-account")
    suspend fun verifyAccountOtp(@Body request: Map<String, String>): Response<ApiResponse<Boolean>>

    @POST("auth/resend-verification")
    suspend fun resendVerification(@Query("email") email: String): Response<ApiResponse<Unit>>
}