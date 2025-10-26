package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.NovelDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.NovelSearchRequest
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface NovelApiService {

    @GET("novels")
    suspend fun getAllNovels(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<NovelDto>>

    @GET("novels/{id}")
    suspend fun getNovelById(@Path("id") id: String): ApiResponse<NovelDto>

    // Home screen specific endpoints
    @GET("novels/top/view-count")
    suspend fun getBannerNovels(): ApiResponse<List<NovelDto>>

    @GET("novels/top/follow-count")
    suspend fun getRecommendedNovels(): ApiResponse<List<NovelDto>>

    @GET("novels/top/rating")
    suspend fun getRankingNovels(): ApiResponse<List<NovelDto>>

    @GET("novels/recent")
    suspend fun getNewNovels(): ApiResponse<List<NovelDto>>

    @GET("novels")
    suspend fun getCompletedNovels(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("status") status: String = "COMPLETED"
    ): ApiResponse<PageResponse<NovelDto>>

    // Legacy endpoints (kept for backward compatibility)
    @GET("novels/top/view-count")
    suspend fun getTopNovelsByViewCount(): ApiResponse<List<NovelDto>>

    @GET("novels/top/follow-count")
    suspend fun getTopNovelsByFollowCount(): ApiResponse<List<NovelDto>>

    @GET("novels/top/rating")
    suspend fun getTopNovelsByRating(): ApiResponse<List<NovelDto>>

    @GET("novels/recent")
    suspend fun getRecentlyUpdatedNovels(): ApiResponse<List<NovelDto>>

    // Author endpoints
    @Multipart
    @POST("novels")
    suspend fun createNovel(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("authorName") authorName: RequestBody,
        @Part("authorId") authorId: RequestBody?,
        @Part("categories") categories: RequestBody,
        @Part("status") status: RequestBody,
        @Part("isR18") isR18: RequestBody,
        @Part coverImage: MultipartBody.Part?,
        @Part("coverUrl") coverUrl: RequestBody?
    ): Response<ApiResponse<NovelDto>>

    @Multipart
    @PUT("novels/{id}")
    suspend fun updateNovel(
        @Path("id") id: String,
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("authorName") authorName: RequestBody?,
        @Part("authorId") authorId: RequestBody?,
        @Part("categories") categories: RequestBody?,
        @Part("rating") rating: RequestBody?,
        @Part("wordCount") wordCount: RequestBody?,
        @Part("chapterCount") chapterCount: RequestBody?,
        @Part("status") status: RequestBody?,
        @Part("isR18") isR18: RequestBody?,
        @Part coverImage: MultipartBody.Part?,
        @Part("coverUrl") coverUrl: RequestBody?
    ): Response<ApiResponse<NovelDto>>

    @DELETE("novels/{id}")
    suspend fun deleteNovel(@Path("id") id: String): Response<ApiResponse<Nothing>>

    @GET("novels/author/{authorId}")
    suspend fun getNovelsByAuthor(
        @Path("authorId") authorId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<NovelDto>>>

    @GET("novels/{novelId}/recommendations")
    suspend fun getRecommendationsByNovel(
        @Path("novelId") novelId: String,
        @Query("limit") limit: Int = 5
    ): ApiResponse<List<NovelDto>>

    @POST("novels/search")
    suspend fun searchNovels(@Body request: NovelSearchRequest): ApiResponse<PageResponse<NovelDto>>
}
