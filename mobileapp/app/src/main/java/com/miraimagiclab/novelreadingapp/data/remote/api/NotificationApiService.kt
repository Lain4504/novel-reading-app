package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.NotificationDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import retrofit2.http.*

interface NotificationApiService {

    @GET("notifications/users/{userId}")
    suspend fun getNotificationsByUserId(
        @Path("userId") userId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<NotificationDto>>

    @GET("notifications/{id}")
    suspend fun getNotificationById(
        @Path("id") id: String
    ): ApiResponse<NotificationDto>

    @POST("notifications/{id}/read")
    suspend fun markAsRead(
        @Path("id") id: String
    ): ApiResponse<NotificationDto>

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(
        @Path("id") id: String
    ): ApiResponse<Nothing>
}

