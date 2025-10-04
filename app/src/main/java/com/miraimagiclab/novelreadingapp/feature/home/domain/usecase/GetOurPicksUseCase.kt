package com.miraimagiclab.novelreadingapp.feature.home.domain.usecase

import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.PickBook
import com.miraimagiclab.novelreadingapp.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOurPicksUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(category: String = "novel", limit: Int = 10): Flow<ApiResult<List<PickBook>>> {
        return repository.getOurPicks(category, limit)
    }
}
