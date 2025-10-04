package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.api.ApiService
import com.miraimagiclab.novelreadingapp.data.model.ApiResult
import com.miraimagiclab.novelreadingapp.data.model.Book
import com.miraimagiclab.novelreadingapp.data.model.FeaturedBook
import com.miraimagiclab.novelreadingapp.data.model.PickBook
import com.miraimagiclab.novelreadingapp.data.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getUserStats(userId: String = "defaultUserId"): Flow<ApiResult<UserStats>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = apiService.getUserStats(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                val userStats = response.body()!!.data
                if (userStats != null) {
                    emit(ApiResult.Success(userStats))
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

    fun getFeaturedBook(): Flow<ApiResult<FeaturedBook>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = apiService.getFeaturedNovel()
            if (response.isSuccessful && response.body()?.success == true) {
                val featuredData = response.body()!!.data
                if (featuredData != null) {
                    emit(ApiResult.Success(featuredData))
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

    fun getRecommendedBooks(userId: String? = null, limit: Int = 10): Flow<ApiResult<List<Book>>> = flow {
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
                // For now return error until proper parsing is implemented
                emit(ApiResult.Error("Novel data parsing not implemented yet"))
            } else {
                val errorMsg = response.body()?.message ?: "Failed to fetch recommended books"
                emit(ApiResult.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Network error occurred"))
        }
    }

    fun getOurPicks(category: String = "novel", limit: Int = 10): Flow<ApiResult<List<PickBook>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = apiService.getOurPicks(category, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                val picksData = response.body()!!.data
                if (picksData != null) {
                    emit(ApiResult.Success(picksData))
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