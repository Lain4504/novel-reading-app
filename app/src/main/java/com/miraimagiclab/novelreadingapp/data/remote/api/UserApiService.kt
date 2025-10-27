package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.LoginResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<ApiResponse<UserDto>>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UserUpdateRequest
    ): Response<ApiResponse<UserDto>>

    @POST("users/{id}/upgrade-to-author")
    suspend fun upgradeToAuthor(@Path("id") id: String): Response<ApiResponse<LoginResponse>>

    @PUT("users/{id}/change-password")
    suspend fun changePassword(
        @Path("id") id: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<Unit>>
}
