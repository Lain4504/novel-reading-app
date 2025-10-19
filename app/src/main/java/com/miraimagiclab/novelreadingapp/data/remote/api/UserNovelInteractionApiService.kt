package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserNovelInteractionDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserNovelInteractionApiService {

    @GET("interactions/users/{userId}/novels/{novelId}")
    suspend fun getUserNovelInteraction(
        @Path("userId") userId: String,
        @Path("novelId") novelId: String
    ): ApiResponse<UserNovelInteractionDto?>

    @GET("interactions/users/{userId}")
    suspend fun getUserInteractions(
        @Path("userId") userId: String
    ): ApiResponse<List<UserNovelInteractionDto>>

    @POST("interactions/users/{userId}/novels/{novelId}/read")
    suspend fun updateReadingProgress(
        @Path("userId") userId: String,
        @Path("novelId") novelId: String,
        @Query("chapterNumber") chapterNumber: Int,
        @Query("chapterId") chapterId: String
    ): ApiResponse<UserNovelInteractionDto>

    @POST("interactions/users/{userId}/novels/{novelId}/follow")
    suspend fun toggleFollow(
        @Path("userId") userId: String,
        @Path("novelId") novelId: String
    ): ApiResponse<UserNovelInteractionDto>

    @POST("interactions/users/{userId}/novels/{novelId}/wishlist")
    suspend fun toggleWishlist(
        @Path("userId") userId: String,
        @Path("novelId") novelId: String
    ): ApiResponse<UserNovelInteractionDto>

    @GET("interactions/users/{userId}/following")
    suspend fun getUserFollowingList(
        @Path("userId") userId: String
    ): ApiResponse<List<UserNovelInteractionDto>>

    @GET("interactions/users/{userId}/wishlist")
    suspend fun getUserWishlist(
        @Path("userId") userId: String
    ): ApiResponse<List<UserNovelInteractionDto>>
}
