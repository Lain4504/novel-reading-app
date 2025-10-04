package com.miraimagiclab.novelreadingapp.data.api

import com.miraimagiclab.novelreadingapp.data.model.Book
import com.miraimagiclab.novelreadingapp.data.model.FeaturedBook
import com.miraimagiclab.novelreadingapp.data.model.PickBook
import com.miraimagiclab.novelreadingapp.data.model.UserStats
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    
    @GET("user/stats")
    suspend fun getUserStats(): UserStats
    
    @GET("books/banner")
    suspend fun getFeaturedBook(): FeaturedBook
    
    @GET("books/recommended")
    suspend fun getRecommendedBooks(
        @Query("user_id") userId: String? = null,
        @Query("limit") limit: Int = 10
    ): List<Book>
    
    @GET("books/our-picks")
    suspend fun getOurPicks(
        @Query("category") category: String = "novel",
        @Query("limit") limit: Int = 10
    ): List<PickBook>
}
