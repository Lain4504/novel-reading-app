package com.miraimagiclab.novelreadingapp.domain.repository

import com.miraimagiclab.novelreadingapp.domain.model.Novel
import kotlinx.coroutines.flow.Flow

interface NovelRepository {
    
    fun getTopNovelsByRating(): Flow<List<Novel>>
    fun getTopNovelsByFollowCount(): Flow<List<Novel>>
    fun getTopNovelsByViewCount(): Flow<List<Novel>>
    fun getRecentlyUpdatedNovels(): Flow<List<Novel>>
    fun getCompletedNovels(): Flow<List<Novel>>
    
    suspend fun refreshTopNovelsByRating()
    suspend fun refreshTopNovelsByFollowCount()
    suspend fun refreshTopNovelsByViewCount()
    suspend fun refreshRecentlyUpdatedNovels()
    suspend fun refreshCompletedNovels()
    
    suspend fun getNovelById(id: String): Novel?
}
