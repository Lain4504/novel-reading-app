package com.miraimagiclab.novelreadingapp.domain.repository

import com.miraimagiclab.novelreadingapp.domain.model.Novel
import kotlinx.coroutines.flow.Flow

interface NovelRepository {
    
    // Home screen specific data flows
    fun getBannerNovels(): Flow<List<Novel>>
    fun getRecommendedNovels(): Flow<List<Novel>>
    fun getRankingNovels(): Flow<List<Novel>>
    fun getNewNovels(): Flow<List<Novel>>
    fun getCompletedNovels(): Flow<List<Novel>>
    
    // Home screen specific refresh methods
    suspend fun refreshBannerNovels()
    suspend fun refreshRecommendedNovels()
    suspend fun refreshRankingNovels()
    suspend fun refreshNewNovels()
    suspend fun refreshCompletedNovels()
    
    // Legacy methods (kept for backward compatibility)
    fun getTopNovelsByRating(): Flow<List<Novel>>
    fun getTopNovelsByFollowCount(): Flow<List<Novel>>
    fun getTopNovelsByViewCount(): Flow<List<Novel>>
    fun getRecentlyUpdatedNovels(): Flow<List<Novel>>
    
    suspend fun refreshTopNovelsByRating()
    suspend fun refreshTopNovelsByFollowCount()
    suspend fun refreshTopNovelsByViewCount()
    suspend fun refreshRecentlyUpdatedNovels()
    
    suspend fun getNovelById(id: String): Novel?
    
    // Get multiple novels by their IDs
    suspend fun getNovelsByIds(ids: List<String>): List<Novel>
}
