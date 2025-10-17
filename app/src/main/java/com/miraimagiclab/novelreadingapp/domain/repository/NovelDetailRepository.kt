package com.miraimagiclab.novelreadingapp.domain.repository

import com.miraimagiclab.novelreadingapp.data.BookDetail
import kotlinx.coroutines.flow.Flow

interface NovelDetailRepository {
    
    fun getNovelDetail(id: String): Flow<BookDetail?>
    
    suspend fun refreshNovelDetail(id: String)
}


