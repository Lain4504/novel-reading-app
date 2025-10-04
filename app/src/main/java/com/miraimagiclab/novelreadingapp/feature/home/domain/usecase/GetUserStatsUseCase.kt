package com.miraimagiclab.novelreadingapp.feature.home.domain.usecase

import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.UserStats
import com.miraimagiclab.novelreadingapp.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserStatsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(userId: String = "defaultUserId"): Flow<ApiResult<UserStats>> {
        return repository.getUserStats(userId)
    }
}
