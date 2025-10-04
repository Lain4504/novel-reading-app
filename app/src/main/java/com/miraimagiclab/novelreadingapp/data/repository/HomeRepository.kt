package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.api.ApiService
import com.miraimagiclab.novelreadingapp.data.model.ApiResult
import com.miraimagiclab.novelreadingapp.data.model.Book
import com.miraimagiclab.novelreadingapp.data.model.FeaturedBook
import com.miraimagiclab.novelreadingapp.data.model.PickBook
import com.miraimagiclab.novelreadingapp.data.model.UserStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getUserStats(): Flow<ApiResult<UserStats>> = flow {
        emit(ApiResult.Loading)
        try {
            // Simulate network delay
            delay(1000)
            
            // Try to call real API first, fallback to mock data if fails
            try {
                val response = apiService.getUserStats()
                emit(ApiResult.Success(response))
            } catch (e: Exception) {
                // Fallback to mock data
                val mockData = UserStats(
                    bookPoints = 1200,
                    readBooks = 22,
                    userName = "Cheyenne Curtis"
                )
                emit(ApiResult.Success(mockData))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getFeaturedBook(): Flow<ApiResult<FeaturedBook>> = flow {
        emit(ApiResult.Loading)
        try {
            delay(800)
            
            try {
                val response = apiService.getFeaturedBook()
                emit(ApiResult.Success(response))
            } catch (e: Exception) {
                // Fallback to mock data
                val mockData = FeaturedBook(
                    title = "Eighty Six",
                    coverUrl = "https://example.com/cover.jpg",
                    series = "Eighty Six"
                )
                emit(ApiResult.Success(mockData))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getRecommendedBooks(userId: String? = null, limit: Int = 10): Flow<ApiResult<List<Book>>> = flow {
        emit(ApiResult.Loading)
        try {
            delay(1200)
            
            try {
                val response = apiService.getRecommendedBooks(userId, limit)
                emit(ApiResult.Success(response))
            } catch (e: Exception) {
                // Fallback to mock data
                val mockData = listOf(
                    Book(
                        id = "1",
                        title = "Maze Runner: The Scorch Trials",
                        type = "Novel",
                        score = 86,
                        coverUrl = "https://example.com/cover1.jpg",
                        genres = listOf("Action", "Sci-fi")
                    ),
                    Book(
                        id = "2",
                        title = "The Hunger Games",
                        type = "Novel",
                        score = 92,
                        coverUrl = "https://example.com/cover2.jpg",
                        genres = listOf("Dystopian", "Action")
                    ),
                    Book(
                        id = "3",
                        title = "Divergent",
                        type = "Novel",
                        score = 78,
                        coverUrl = "https://example.com/cover3.jpg",
                        genres = listOf("Dystopian", "Romance")
                    )
                )
                emit(ApiResult.Success(mockData))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getOurPicks(category: String = "novel", limit: Int = 10): Flow<ApiResult<List<PickBook>>> = flow {
        emit(ApiResult.Loading)
        try {
            delay(1000)
            
            try {
                val response = apiService.getOurPicks(category, limit)
                emit(ApiResult.Success(response))
            } catch (e: Exception) {
                // Fallback to mock data
                val mockData = listOf(
                    PickBook(
                        id = "1",
                        title = "The Alchemist",
                        author = "Paulo Coelho",
                        coverUrl = "https://example.com/alchemist.jpg"
                    ),
                    PickBook(
                        id = "2",
                        title = "1984",
                        author = "George Orwell",
                        coverUrl = "https://example.com/1984.jpg"
                    ),
                    PickBook(
                        id = "3",
                        title = "To Kill a Mockingbird",
                        author = "Harper Lee",
                        coverUrl = "https://example.com/mockingbird.jpg"
                    )
                )
                emit(ApiResult.Success(mockData))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error occurred"))
        }
    }
}
