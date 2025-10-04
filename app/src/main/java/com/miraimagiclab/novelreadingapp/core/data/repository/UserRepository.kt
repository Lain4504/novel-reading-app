package com.miraimagiclab.novelreadingapp.core.data.repository

import com.miraimagiclab.novelreadingapp.core.data.api.ApiService
import com.miraimagiclab.novelreadingapp.core.data.model.User
import com.miraimagiclab.novelreadingapp.core.data.model.UserStats
import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shared User repository for all user-related operations
 * Used by profile, auth, settings features
 */
@Singleton
class UserRepository @Inject constructor(
    apiService: ApiService
) : BaseRepository(apiService) {

    fun getUserStats(userId: String = "defaultUserId"): Flow<ApiResult<UserStats>> = safeApiCall {
        apiService.getUserStats(userId)
    }

    fun getCurrentUser(): Flow<ApiResult<User>> = safeApiCall {
        // TODO: Add get current user endpoint
        apiService.getUserStats("defaultUserId") // Temporary
    }

    fun updateUserProfile(user: User): Flow<ApiResult<User>> = safeApiCall {
        // TODO: Add update user profile endpoint
        apiService.getUserStats("defaultUserId") // Temporary
    }
}
