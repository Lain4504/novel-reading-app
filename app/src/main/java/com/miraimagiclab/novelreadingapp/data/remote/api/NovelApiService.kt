package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.NovelDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NovelApiService {

    @GET("novels")
    suspend fun getAllNovels(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<NovelDto>>

    @GET("novels/{id}")
    suspend fun getNovelById(@Path("id") id: String): ApiResponse<NovelDto>

    @GET("novels/top/view-count")
    suspend fun getTopNovelsByViewCount(): ApiResponse<List<NovelDto>>

    @GET("novels/top/follow-count")
    suspend fun getTopNovelsByFollowCount(): ApiResponse<List<NovelDto>>

    @GET("novels/top/rating")
    suspend fun getTopNovelsByRating(): ApiResponse<List<NovelDto>>

    @GET("novels/recent")
    suspend fun getRecentlyUpdatedNovels(): ApiResponse<List<NovelDto>>

    @GET("novels")
    suspend fun getCompletedNovels(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("status") status: String = "COMPLETED"
    ): ApiResponse<PageResponse<NovelDto>>
}
