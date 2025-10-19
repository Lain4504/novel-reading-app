package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.remote.api.UserApiService
import com.miraimagiclab.novelreadingapp.data.remote.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserUpdateRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApiService: UserApiService
) {
    suspend fun getUserById(id: String): Result<UserDto> {
        return try {
            val response = userApiService.getUserById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to get user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestAuthorRole(userId: String): Result<UserDto> {
        return try {
            val request = UserUpdateRequest(roles = setOf("AUTHOR"))
            val response = userApiService.updateUser(userId, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to request author role"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
