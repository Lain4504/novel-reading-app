package com.miraimagiclab.novelreadingapp.data.api

import com.miraimagiclab.novelreadingapp.data.model.ApiResponse
import com.miraimagiclab.novelreadingapp.data.model.Book
import com.miraimagiclab.novelreadingapp.data.model.FeaturedBook
import com.miraimagiclab.novelreadingapp.data.model.PickBook
import com.miraimagiclab.novelreadingapp.data.model.UserStats
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    
    // Health check endpoint
    @GET("health")
    suspend fun getHealth(): Response<ApiResponse<Map<String, Any>>>
    
    // User endpoints
    @GET("users/{userId}/stats")
    suspend fun getUserStats(@Path("userId") userId: String): Response<ApiResponse<UserStats>>
    
    // Novel endpoints - updated to match backend
    @GET("novels/featured")
    suspend fun getFeaturedNovel(): Response<ApiResponse<FeaturedBook>>
    
    @GET("novels")
    suspend fun getRecommendedBooks(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "createdAt",
        @Query("sortDirection") sortDirection: String = "DESC"
    ): Response<ApiResponse<Any>> // Backend returns PageResponse<NovelDto>
    
    @GET("novels/our-picks")
    suspend fun getOurPicks(
        @Query("category") category: String = "novel",
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<PickBook>>>
}
