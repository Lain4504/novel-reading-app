package com.miraimagiclab.novelreadingapp.feature.home.data.repository

import com.miraimagiclab.novelreadingapp.core.network.ApiService
import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.Book
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.FeaturedBook
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.PickBook
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.UserStats
import com.miraimagiclab.novelreadingapp.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : HomeRepository {

    override fun getUserStats(userId: String): Flow<ApiResult<UserStats>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = apiService.getUserStats(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                val userStats = response.body()!!.data
                if (userStats != null) {
                    // TODO: Map from API response to domain entity
                    // For now, return mock data
                    val domainUserStats = UserStats(
                        totalBooksRead = 5,
                        totalReadingTime = 120,
                        currentStreak = 3,
                        favoriteGenre = "Fantasy"
                    )
                    emit(ApiResult.Success(domainUserStats))
                } else {
                    emit(ApiResult.Error("User stats data is null"))
                }
            } else {
                val errorMsg = response.body()?.message ?: "Failed to fetch user stats"
                emit(ApiResult.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Network error occurred"))
        }
    }

    override fun getFeaturedBook(): Flow<ApiResult<FeaturedBook>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = apiService.getFeaturedNovel()
            if (response.isSuccessful && response.body()?.success == true) {
                val featuredData = response.body()!!.data
                if (featuredData != null) {
                    // TODO: Map from API response to domain entity
                    // For now, return mock data
                    val domainFeaturedBook = FeaturedBook(
                        id = "1",
                        title = "The Great Adventure",
                        author = "John Doe",
                        coverImageUrl = null,
                        description = "An epic fantasy adventure",
                        genre = "Fantasy",
                        rating = 4.5,
                        totalChapters = 25,
                        isCompleted = false,
                        featuredReason = "Editor's Choice"
                    )
                    emit(ApiResult.Success(domainFeaturedBook))
                } else {
                    emit(ApiResult.Error("Featured book data is null"))
                }
            } else {
                val errorMsg = response.body()?.message ?: "Failed to fetch featured book"
                emit(ApiResult.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Network error occurred"))
        }
    }

    override fun getRecommendedBooks(userId: String?, limit: Int): Flow<ApiResult<List<Book>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = apiService.getRecommendedBooks(
                page = 0,
                size = limit,
                sortBy = "createdAt",
                sortDirection = "DESC"
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val responseData = response.body()!!.data
                // TODO: Parse actual Novel data to Book data when backend response structure is finalized
                // For now return mock data
                val mockBooks = listOf(
                    Book(
                        id = "1",
                        title = "Fantasy Quest",
                        author = "Jane Smith",
                        coverImageUrl = null,
                        description = "A magical journey",
                        genre = "Fantasy",
                        rating = 4.2,
                        totalChapters = 20,
                        isCompleted = false
                    ),
                    Book(
                        id = "2",
                        title = "Space Odyssey",
                        author = "Bob Johnson",
                        coverImageUrl = null,
                        description = "Sci-fi adventure",
                        genre = "Sci-Fi",
                        rating = 4.7,
                        totalChapters = 30,
                        isCompleted = true
                    )
                )
                emit(ApiResult.Success(mockBooks))
            } else {
                val errorMsg = response.body()?.message ?: "Failed to fetch recommended books"
                emit(ApiResult.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Network error occurred"))
        }
    }

    override fun getOurPicks(category: String, limit: Int): Flow<ApiResult<List<PickBook>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = apiService.getOurPicks(category, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                val picksData = response.body()!!.data
                if (picksData != null) {
                    // TODO: Map from API response to domain entity
                    // For now, return mock data
                    val mockPicks = listOf(
                        PickBook(
                            id = "1",
                            title = "Mystery Manor",
                            author = "Alice Brown",
                            coverImageUrl = null,
                            description = "A thrilling mystery",
                            genre = "Mystery",
                            rating = 4.8,
                            totalChapters = 15,
                            isCompleted = false,
                            pickReason = "Editor's Pick"
                        ),
                        PickBook(
                            id = "2",
                            title = "Romance in Paris",
                            author = "Charlie Wilson",
                            coverImageUrl = null,
                            description = "A love story",
                            genre = "Romance",
                            rating = 4.3,
                            totalChapters = 18,
                            isCompleted = true,
                            pickReason = "Reader's Choice"
                        )
                    )
                    emit(ApiResult.Success(mockPicks))
                } else {
                    emit(ApiResult.Error("Our picks data is null"))
                }
            } else {
                val errorMsg = response.body()?.message ?: "Failed to fetch our picks"
                emit(ApiResult.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Network error occurred"))
        }
    }
}
