package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.mapper.UserNovelInteractionMapper
import com.miraimagiclab.novelreadingapp.data.remote.api.UserNovelInteractionApiService
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.model.UserNovelInteraction
import com.miraimagiclab.novelreadingapp.domain.repository.NovelRepository
import com.miraimagiclab.novelreadingapp.domain.repository.UserNovelInteractionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserNovelInteractionRepositoryImpl @Inject constructor(
    private val interactionApiService: UserNovelInteractionApiService,
    private val novelRepository: NovelRepository
) : UserNovelInteractionRepository {

    override suspend fun getUserInteractions(userId: String): List<UserNovelInteraction> {
        return try {
            val response = interactionApiService.getUserInteractions(userId)
            if (response.success && response.data != null) {
                response.data.map { UserNovelInteractionMapper.mapDtoToDomain(it) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUserFollowingNovels(userId: String): List<Novel> {
        return try {
            val response = interactionApiService.getUserFollowingList(userId)
            if (response.success && response.data != null) {
                val novelIds = response.data.map { it.novelId }
                // Get novel details from novel repository
                novelRepository.getNovelsByIds(novelIds)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUserInProgressNovels(userId: String): List<Novel> {
        return try {
            val response = interactionApiService.getUserInteractions(userId)
            if (response.success && response.data != null) {
                // Filter interactions that have reading progress
                val inProgressInteractions = response.data.filter { 
                    it.lastReadAt != null || it.currentChapterId != null 
                }
                val novelIds = inProgressInteractions.map { it.novelId }
                // Get novel details from novel repository
                novelRepository.getNovelsByIds(novelIds)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUserWishlistNovels(userId: String): List<Novel> {
        return try {
            val response = interactionApiService.getUserWishlist(userId)
            if (response.success && response.data != null) {
                val novelIds = response.data.map { it.novelId }
                // Get novel details from novel repository
                novelRepository.getNovelsByIds(novelIds)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUserNovelInteraction(userId: String, novelId: String): UserNovelInteraction? {
        return try {
            val response = interactionApiService.getUserNovelInteraction(userId, novelId)
            if (response.success && response.data != null) {
                UserNovelInteractionMapper.mapDtoToDomain(response.data)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun toggleFollow(userId: String, novelId: String): UserNovelInteraction {
        val response = interactionApiService.toggleFollow(userId, novelId)
        if (response.success && response.data != null) {
            return UserNovelInteractionMapper.mapDtoToDomain(response.data)
        } else {
            throw Exception("Failed to toggle follow")
        }
    }

    override suspend fun toggleWishlist(userId: String, novelId: String): UserNovelInteraction {
        val response = interactionApiService.toggleWishlist(userId, novelId)
        if (response.success && response.data != null) {
            return UserNovelInteractionMapper.mapDtoToDomain(response.data)
        } else {
            throw Exception("Failed to toggle wishlist")
        }
    }

    override suspend fun updateReadingProgress(
        userId: String,
        novelId: String,
        chapterNumber: Int,
        chapterId: String
    ): UserNovelInteraction {
        val response = interactionApiService.updateReadingProgress(userId, novelId, chapterNumber, chapterId)
        if (response.success && response.data != null) {
            return UserNovelInteractionMapper.mapDtoToDomain(response.data)
        } else {
            throw Exception("Failed to update reading progress")
        }
    }

    override suspend fun deleteUserNovelInteraction(userId: String, novelId: String) {
        val response = interactionApiService.deleteUserNovelInteraction(userId, novelId)
        if (!response.success) {
            throw Exception("Failed to delete user novel interaction")
        }
    }
}


