package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.ChapterDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChapterApiService {

    @GET("chapters/{chapterId}")
    suspend fun getChapterById(@Path("chapterId") chapterId: String): ApiResponse<ChapterDto>

    @GET("chapters/novels/{novelId}")
    suspend fun getChaptersByNovelId(
        @Path("novelId") novelId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sortDirection") sortDirection: String = "asc"
    ): ApiResponse<PageResponse<ChapterDto>>
}


