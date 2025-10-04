package com.miraimagiclab.novelreadingapp.feature.home.domain.usecase

import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.Book
import com.miraimagiclab.novelreadingapp.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecommendedBooksUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(userId: String? = null, limit: Int = 10): Flow<ApiResult<List<Book>>> {
        return repository.getRecommendedBooks(userId, limit)
    }
}
