package com.miraimagiclab.novelreadingapp.feature.home.domain.repository

import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.Book
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.FeaturedBook
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.PickBook
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.UserStats
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getUserStats(userId: String = "defaultUserId"): Flow<ApiResult<UserStats>>
    fun getFeaturedBook(): Flow<ApiResult<FeaturedBook>>
    fun getRecommendedBooks(userId: String? = null, limit: Int = 10): Flow<ApiResult<List<Book>>>
    fun getOurPicks(category: String = "novel", limit: Int = 10): Flow<ApiResult<List<PickBook>>>
}
