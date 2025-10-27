package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.CommentDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.request.CommentCreateRequestDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.request.CommentReplyCreateRequestDto
import retrofit2.http.*

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

    @POST("comments")
    suspend fun createComment(@Body request: CommentCreateRequestDto): ApiResponse<CommentDto>

    @POST("comments/{commentId}/reply")
    suspend fun createReply(
        @Path("commentId") commentId: String,
        @Body request: CommentReplyCreateRequestDto
    ): ApiResponse<CommentDto>
}


