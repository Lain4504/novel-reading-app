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

    // Home screen specific implementations - now calling API directly
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

    // Refresh methods are no longer needed since we call API directly

    // Legacy implementations - now calling API directly
    override fun getTopNovelsByRating(): Flow<List<Novel>> {
        return flow {
            try {
                val response = novelApiService.getTopNovelsByRating()
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

    override fun getTopNovelsByFollowCount(): Flow<List<Novel>> {
        return flow {
            try {
                val response = novelApiService.getTopNovelsByFollowCount()
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

    override fun getTopNovelsByViewCount(): Flow<List<Novel>> {
        return flow {
            try {
                val response = novelApiService.getTopNovelsByViewCount()
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

    override fun getRecentlyUpdatedNovels(): Flow<List<Novel>> {
        return flow {
            try {
                val response = novelApiService.getRecentlyUpdatedNovels()
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

    // All refresh methods removed - no longer needed with direct API calls

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
        println("DEBUG: getNovelsByIds called with IDs: $ids")
        val fetchedNovels = mutableListOf<Novel>()
        
        // Fetch all novels from API directly
        ids.forEach { novelId ->
            try {
                println("DEBUG: Fetching novel with ID: $novelId")
                val response = novelApiService.getNovelById(novelId)
                if (response.success && response.data != null) {
                    val novel = NovelMapper.mapDtoToDomain(response.data)
                    fetchedNovels.add(novel)
                    println("DEBUG: Successfully fetched novel: ${novel.title}")
                } else {
                    println("DEBUG: Failed to fetch novel $novelId: ${response.message}")
                }
            } catch (e: Exception) {
                println("DEBUG: Exception fetching novel $novelId: ${e.message}")
                e.printStackTrace()
            }
        }
        
        println("DEBUG: Total novels returned: ${fetchedNovels.size}")
        return fetchedNovels
    }

    override suspend fun searchNovels(query: String, page: Int, size: Int, sortBy: String, sortDirection: String): PageResponse<Novel> {
        return try {
            // Always use search API to support sort options
            val searchRequest = NovelSearchRequest(
                title = if (query.isBlank()) null else query,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
            val response = novelApiService.searchNovels(searchRequest)
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
            // Log error for debugging
            println("Error in searchNovels: ${e.message}")
            e.printStackTrace()
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
