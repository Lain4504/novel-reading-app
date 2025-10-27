package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.ReviewDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.request.ReviewCreateRequestDto
import retrofit2.http.*

interface ReviewApiService {

    @GET("reviews/{id}")
    suspend fun getReviewById(@Path("id") id: String): ApiResponse<ReviewDto>

    @GET("reviews/novels/{novelId}")
    suspend fun getReviewsByNovel(
        @Path("novelId") novelId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<ReviewDto>>

    @GET("reviews/novels/{novelId}/top")
    suspend fun getTopReviewsByNovel(
        @Path("novelId") novelId: String,
        @Query("limit") limit: Int = 10
    ): ApiResponse<List<ReviewDto>>

    @GET("reviews/novels/{novelId}/average-rating")
    suspend fun getAverageRatingByNovel(@Path("novelId") novelId: String): ApiResponse<Double?>

    @GET("reviews/novels/{novelId}/count")
    suspend fun getReviewCountByNovel(@Path("novelId") novelId: String): ApiResponse<Long>

    @POST("reviews")
    suspend fun createReview(@Body request: ReviewCreateRequestDto): ApiResponse<ReviewDto>
}


