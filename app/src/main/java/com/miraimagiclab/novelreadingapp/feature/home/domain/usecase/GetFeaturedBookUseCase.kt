package com.miraimagiclab.novelreadingapp.feature.home.domain.usecase

import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.FeaturedBook
import com.miraimagiclab.novelreadingapp.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeaturedBookUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<ApiResult<FeaturedBook>> {
        return repository.getFeaturedBook()
    }
}
