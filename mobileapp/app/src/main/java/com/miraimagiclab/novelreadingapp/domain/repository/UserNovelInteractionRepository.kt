package com.miraimagiclab.novelreadingapp.domain.repository

import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.model.UserNovelInteraction
import kotlinx.coroutines.flow.Flow

interface UserNovelInteractionRepository {
    
    /**
     * Get all interactions for a specific user
     */
    suspend fun getUserInteractions(userId: String): List<UserNovelInteraction>
    
    /**
     * Get all novels that user is following
     * Returns novels with full detail, not just interactions
     */
    suspend fun getUserFollowingNovels(userId: String): List<Novel>
    
    /**
     * Get all novels that user is currently reading (has reading progress)
     * Returns novels with full detail, not just interactions
     */
    suspend fun getUserInProgressNovels(userId: String): List<Novel>
    
    /**
     * Get all novels in user's wishlist
     * Returns novels with full detail, not just interactions
     */
    suspend fun getUserWishlistNovels(userId: String): List<Novel>
    
    /**
     * Get interaction for a specific user and novel
     */
    suspend fun getUserNovelInteraction(userId: String, novelId: String): UserNovelInteraction?
    
    /**
     * Toggle follow status for a novel
     */
    suspend fun toggleFollow(userId: String, novelId: String): UserNovelInteraction
    
    /**
     * Toggle wishlist status for a novel
     */
    suspend fun toggleWishlist(userId: String, novelId: String): UserNovelInteraction
    
    /**
     * Update reading progress
     */
    suspend fun updateReadingProgress(
        userId: String,
        novelId: String,
        chapterNumber: Int,
        chapterId: String
    ): UserNovelInteraction
    
    /**
     * Delete user novel interaction (unfollow and remove from lists)
     */
    suspend fun deleteUserNovelInteraction(userId: String, novelId: String)
}


