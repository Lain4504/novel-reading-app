package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.ChapterDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.ChapterCreateRequest
import com.miraimagiclab.novelreadingapp.data.remote.dto.ChapterUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface ChapterApiService {

    @GET("chapters/novels/{novelId}/{chapterId}")
    suspend fun getChapterById(
        @Path("novelId") novelId: String,
        @Path("chapterId") chapterId: String
    ): Response<ApiResponse<ChapterDto>>

    @GET("chapters/novels/{novelId}")
    suspend fun getChaptersByNovelId(
        @Path("novelId") novelId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sortDirection") sortDirection: String = "asc"
    ): ApiResponse<PageResponse<ChapterDto>>

    // Author endpoints
    @POST("chapters/novels/{novelId}")
    suspend fun createChapter(
        @Path("novelId") novelId: String,
        @Body request: ChapterCreateRequest
    ): Response<ApiResponse<ChapterDto>>

    @PUT("chapters/novels/{novelId}/{chapterId}")
    suspend fun updateChapter(
        @Path("novelId") novelId: String,
        @Path("chapterId") chapterId: String,
        @Body request: ChapterUpdateRequest
    ): Response<ApiResponse<ChapterDto>>

    @DELETE("chapters/{chapterId}")
    suspend fun deleteChapter(@Path("chapterId") chapterId: String): Response<ApiResponse<Nothing>>

    @POST("chapters/{chapterId}/increment-view")
    suspend fun incrementChapterViewCount(@Path("chapterId") chapterId: String): ApiResponse<ChapterDto>
}


