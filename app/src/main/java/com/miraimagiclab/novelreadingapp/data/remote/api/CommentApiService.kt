package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.CommentDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApiService {

    @GET("comments/{id}")
    suspend fun getCommentById(@Path("id") id: String): ApiResponse<CommentDto>

    @GET("comments/novel/{novelId}")
    suspend fun getCommentsByNovel(
        @Path("novelId") novelId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<CommentDto>>

    @GET("comments/{commentId}/replies")
    suspend fun getRepliesByCommentId(
        @Path("commentId") commentId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ApiResponse<PageResponse<CommentDto>>
}


