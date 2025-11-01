package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.mapper.NovelMapper
import com.miraimagiclab.novelreadingapp.data.remote.api.NovelApiService
import com.miraimagiclab.novelreadingapp.data.remote.dto.NovelSearchRequest
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.repository.NovelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NovelRepositoryImpl @Inject constructor(
    private val novelApiService: NovelApiService
) : NovelRepository {

    // Home screen specific implementations - calling API directly (no cache)
    override fun getBannerNovels(): Flow<List<Novel>> {
        return flow {
            try {
                val response = novelApiService.getBannerNovels()
                if (response.success && response.data != null) {
                    val novels = response.data.map { NovelMapper.mapDtoToDomain(it) }
                    emit(novels)
                } else {
                    emit(emptyList())
                }
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }

    override fun getRecommendedNovels(): Flow<List<Novel>> {
        return flow {
            try {
                val response = novelApiService.getRecommendedNovels()
                if (response.success && response.data != null) {
                    val novels = response.data.map { NovelMapper.mapDtoToDomain(it) }
                    emit(novels)
                } else {
                    emit(emptyList())
                }
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }

    override fun getRankingNovels(): Flow<List<Novel>> {
        return flow {
            try {
                val response = novelApiService.getRankingNovels()
                if (response.success && response.data != null) {
                    val novels = response.data.map { NovelMapper.mapDtoToDomain(it) }
                    emit(novels)
                } else {
                    emit(emptyList())
                }
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }

    override fun getNewNovels(): Flow<List<Novel>> {
        return flow {
            try {
                val response = novelApiService.getNewNovels()
                if (response.success && response.data != null) {
                    val novels = response.data.map { NovelMapper.mapDtoToDomain(it) }
                    emit(novels)
                } else {
                    emit(emptyList())
                }
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }

    override fun getCompletedNovels(): Flow<List<Novel>> {
        return flow {
            try {
                val response = novelApiService.getCompletedNovels()
                if (response.success && response.data != null) {
                    val novels = response.data.content.map { NovelMapper.mapDtoToDomain(it) }
                    emit(novels)
                } else {
                    emit(emptyList())
                }
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }

    // Refresh methods - no-op since we always call API directly
    override suspend fun refreshBannerNovels() {
        // No-op: we call API directly in getBannerNovels()
    }

    override suspend fun refreshRecommendedNovels() {
        // No-op: we call API directly in getRecommendedNovels()
    }

    override suspend fun refreshRankingNovels() {
        // No-op: we call API directly in getRankingNovels()
    }

    override suspend fun refreshNewNovels() {
        // No-op: we call API directly in getNewNovels()
    }

    override suspend fun refreshCompletedNovels() {
        // No-op: we call API directly in getCompletedNovels()
    }
    
    override suspend fun syncAllNovels() {
        // No-op: we don't use cache anymore, always fetch from API
    }
    
    override suspend fun clearCacheAndRefresh() {
        // No-op: we don't have cache anymore
    }

    // Legacy methods (kept for backward compatibility)
    override fun getTopNovelsByRating(): Flow<List<Novel>> {
        return getRankingNovels()
    }

    override fun getTopNovelsByFollowCount(): Flow<List<Novel>> {
        return getRecommendedNovels()
    }

    override fun getTopNovelsByViewCount(): Flow<List<Novel>> {
        return getBannerNovels()
    }

    override fun getRecentlyUpdatedNovels(): Flow<List<Novel>> {
        return getNewNovels()
    }

    override suspend fun getNovelById(id: String): Novel? {
        return try {
            val response = novelApiService.getNovelById(id)
            if (response.success && response.data != null) {
                NovelMapper.mapDtoToDomain(response.data)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getNovelsByIds(ids: List<String>): List<Novel> {
        return try {
            val novels = mutableListOf<Novel>()
            ids.forEach { novelId ->
                try {
                    val response = novelApiService.getNovelById(novelId)
                    if (response.success && response.data != null) {
                        novels.add(NovelMapper.mapDtoToDomain(response.data))
                    }
                } catch (e: Exception) {
                    // Skip this novel if fetch fails
                }
            }
            novels
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Search functionality
    override suspend fun searchNovels(
        query: String,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: String
    ): PageResponse<Novel> {
        return try {
            val request = NovelSearchRequest(
                title = if (query.isBlank()) null else query,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
            val response = novelApiService.searchNovels(request)
            if (response.success && response.data != null) {
                val novels = response.data.content.map { NovelMapper.mapDtoToDomain(it) }
                PageResponse(
                    content = novels,
                    page = response.data.page,
                    size = response.data.size,
                    totalElements = response.data.totalElements,
                    totalPages = response.data.totalPages,
                    first = response.data.first,
                    last = response.data.last,
                    numberOfElements = response.data.numberOfElements
                )
            } else {
                PageResponse(
                    content = emptyList(),
                    page = 0,
                    size = size,
                    totalElements = 0,
                    totalPages = 0,
                    first = true,
                    last = true,
                    numberOfElements = 0
                )
            }
        } catch (e: Exception) {
            PageResponse(
                content = emptyList(),
                page = 0,
                size = size,
                totalElements = 0,
                totalPages = 0,
                first = true,
                last = true,
                numberOfElements = 0
            )
        }
    }
}
