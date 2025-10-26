package com.miraimagiclab.novelreadingapp.domain.repository

import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import kotlinx.coroutines.flow.Flow

interface NovelRepository {
    
    // Home screen specific data flows
    fun getBannerNovels(): Flow<List<Novel>>
    fun getRecommendedNovels(): Flow<List<Novel>>
    fun getRankingNovels(): Flow<List<Novel>>
    fun getNewNovels(): Flow<List<Novel>>
    fun getCompletedNovels(): Flow<List<Novel>>
    
    // Legacy methods (kept for backward compatibility)
    fun getTopNovelsByRating(): Flow<List<Novel>>
    fun getTopNovelsByFollowCount(): Flow<List<Novel>>
    fun getTopNovelsByViewCount(): Flow<List<Novel>>
    fun getRecentlyUpdatedNovels(): Flow<List<Novel>>
    
    suspend fun getNovelById(id: String): Novel?
    
    // Get multiple novels by their IDs
    suspend fun getNovelsByIds(ids: List<String>): List<Novel>
    
    // Search novels with pagination
    suspend fun searchNovels(query: String, page: Int, size: Int, sortBy: String = "updatedAt", sortDirection: String = "desc"): PageResponse<Novel>
}
