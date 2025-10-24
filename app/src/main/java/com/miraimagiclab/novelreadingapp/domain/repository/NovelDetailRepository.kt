package com.miraimagiclab.novelreadingapp.domain.repository

import com.miraimagiclab.novelreadingapp.domain.model.NovelDetail
import com.miraimagiclab.novelreadingapp.domain.model.UserNovelInteraction
import kotlinx.coroutines.flow.Flow

interface NovelDetailRepository {
    
    fun getNovelDetail(id: String): Flow<NovelDetail?>
    
    suspend fun refreshNovelDetail(id: String)
    
    fun getUserInteraction(userId: String, novelId: String): Flow<UserNovelInteraction?>
    
    suspend fun toggleFollow(userId: String, novelId: String): UserNovelInteraction
    
    suspend fun toggleWishlist(userId: String, novelId: String): UserNovelInteraction
}


