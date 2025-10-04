package com.miraimagiclab.novelreadingapp.core.data.repository

import com.miraimagiclab.novelreadingapp.core.data.api.ApiService
import com.miraimagiclab.novelreadingapp.core.utils.ApiResponse
import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Base repository for common API operations
 * Handles common patterns like error handling, loading states
 */
@Singleton
abstract class BaseRepository @Inject constructor(
    protected val apiService: ApiService
) {
    
    /**
     * Generic method to handle API calls with common error handling
     */
    protected fun <T> safeApiCall(
        apiCall: suspend () -> retrofit2.Response<ApiResponse<T>>
    ): Flow<ApiResult<T>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = apiCall()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data
                if (data != null) {
                    emit(ApiResult.Success(data))
                } else {
                    emit(ApiResult.Error("Data is null"))
                }
            } else {
                val errorMsg = response.body()?.message ?: "API call failed"
                emit(ApiResult.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Network error occurred"))
        }
    }
}
