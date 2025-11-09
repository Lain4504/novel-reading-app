package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class DeviceTokenRequest(
    val token: String,
    val deviceType: String = "android"
)

interface DeviceTokenApiService {
    
    @POST("device-tokens")
    suspend fun saveDeviceToken(
        @Body request: DeviceTokenRequest
    ): ApiResponse<Unit>
    
    @GET("device-tokens")
    suspend fun getUserTokens(): ApiResponse<List<DeviceTokenResponse>>
    
    @DELETE("device-tokens/{token}")
    suspend fun deleteDeviceToken(
        @Path("token") token: String
    ): ApiResponse<Unit>
}

data class DeviceTokenResponse(
    val id: String?,
    val userId: String,
    val token: String,
    val deviceType: String,
    val createdAt: String,
    val updatedAt: String
)

