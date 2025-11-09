package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.RecommendedNovelDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecommendationApiService {
    @GET("recommendations/user/{userId}")
    suspend fun getRecommendations(
        @Path("userId") userId: String,
        @Query("limit") limit: Int = 10
    ): ApiResponse<List<RecommendedNovelDto>>

    @GET("recommendations/topic")
    suspend fun getRecommendationsByTopic(
        @Query("query") query: String,
        @Query("limit") limit: Int = 10
    ): ApiResponse<List<RecommendedNovelDto>>
}