package com.miraimagiclab.novelreadingapp.domain.repository

import com.miraimagiclab.novelreadingapp.domain.model.NovelDetail
import kotlinx.coroutines.flow.Flow

interface NovelDetailRepository {
    
    fun getNovelDetail(id: String): Flow<NovelDetail?>
    
    suspend fun refreshNovelDetail(id: String)
}


