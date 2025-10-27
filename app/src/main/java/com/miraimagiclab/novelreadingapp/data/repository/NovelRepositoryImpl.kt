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

    override suspend fun refreshRankingNovels() {
        try {
            val response = novelApiService.getRankingNovels()
            if (response.success && response.data != null) {
                val novels = response.data.map { NovelMapper.mapDtoToDomain(it) }
                val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                novelDao.insertNovels(entities)
            }
        } catch (e: Exception) {
            // Handle error - data will come from cache
        }
    }

    override suspend fun refreshNewNovels() {
        try {
            val response = novelApiService.getNewNovels()
            if (response.success && response.data != null) {
                val novels = response.data.map { NovelMapper.mapDtoToDomain(it) }
                val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                novelDao.insertNovels(entities)
            }
        } catch (e: Exception) {
            // Handle error - data will come from cache
        }
    }

    override suspend fun refreshCompletedNovels() {
        try {
            val response = novelApiService.getCompletedNovels()
            if (response.success && response.data != null) {
                val novels = response.data.content.map { NovelMapper.mapDtoToDomain(it) }
                val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                novelDao.insertNovels(entities)
            }
        } catch (e: Exception) {
            // Handle error - data will come from cache
        }
    }
    
    override suspend fun syncAllNovels() {
        // Collect all novel IDs from all data sources
        val allNovelIds = mutableSetOf<String>()
        var allFetchesSuccessful = true
        
        try {
            // Fetch banner novels
            try {
                val bannerResponse = novelApiService.getBannerNovels()
                if (bannerResponse.success && bannerResponse.data != null) {
                    val novels = bannerResponse.data.map { NovelMapper.mapDtoToDomain(it) }
                    val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                    novelDao.insertNovels(entities)
                    allNovelIds.addAll(novels.map { it.id })
                }
            } catch (e: Exception) {
                allFetchesSuccessful = false
                android.util.Log.e("NovelRepository", "Error fetching banner novels", e)
            }
            
            // Fetch recommended novels
            try {
                val recommendedResponse = novelApiService.getRecommendedNovels()
                if (recommendedResponse.success && recommendedResponse.data != null) {
                    val novels = recommendedResponse.data.map { NovelMapper.mapDtoToDomain(it) }
                    val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                    novelDao.insertNovels(entities)
                    allNovelIds.addAll(novels.map { it.id })
                }
            } catch (e: Exception) {
                allFetchesSuccessful = false
                android.util.Log.e("NovelRepository", "Error fetching recommended novels", e)
            }
            
            // Fetch ranking novels
            try {
                val rankingResponse = novelApiService.getRankingNovels()
                if (rankingResponse.success && rankingResponse.data != null) {
                    val novels = rankingResponse.data.map { NovelMapper.mapDtoToDomain(it) }
                    val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                    novelDao.insertNovels(entities)
                    allNovelIds.addAll(novels.map { it.id })
                }
            } catch (e: Exception) {
                allFetchesSuccessful = false
                android.util.Log.e("NovelRepository", "Error fetching ranking novels", e)
            }
            
            // Fetch new novels
            try {
                val newResponse = novelApiService.getNewNovels()
                if (newResponse.success && newResponse.data != null) {
                    val novels = newResponse.data.map { NovelMapper.mapDtoToDomain(it) }
                    val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                    novelDao.insertNovels(entities)
                    allNovelIds.addAll(novels.map { it.id })
                }
            } catch (e: Exception) {
                allFetchesSuccessful = false
                android.util.Log.e("NovelRepository", "Error fetching new novels", e)
            }
            
            // Fetch completed novels
            try {
                val completedResponse = novelApiService.getCompletedNovels()
                if (completedResponse.success && completedResponse.data != null) {
                    val novels = completedResponse.data.content.map { NovelMapper.mapDtoToDomain(it) }
                    val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                    novelDao.insertNovels(entities)
                    allNovelIds.addAll(novels.map { it.id })
                }
            } catch (e: Exception) {
                allFetchesSuccessful = false
                android.util.Log.e("NovelRepository", "Error fetching completed novels", e)
            }
            
            // Only delete novels that are not in the fetched list IF all fetches were successful
            // This prevents deleting novels when there's a network error
            if (allFetchesSuccessful && allNovelIds.isNotEmpty()) {
                novelDao.deleteNovelsNotInList(allNovelIds.toList())
                android.util.Log.d("NovelRepository", "Synced ${allNovelIds.size} novels, deleted old ones")
            } else {
                android.util.Log.d("NovelRepository", "Sync incomplete, keeping all cached novels")
            }
        } catch (e: Exception) {
            // Handle unexpected error - keep cache as is
            android.util.Log.e("NovelRepository", "Unexpected error in syncAllNovels", e)
        }
    }
    
    override suspend fun clearCacheAndRefresh() {
        try {
            android.util.Log.d("NovelRepository", "Clearing all cached novels...")
            // Clear all cached novels
            novelDao.deleteAllNovels()
            
            // Fetch all data from server
            val allNovelIds = mutableSetOf<String>()
            
            // Fetch banner novels
            try {
                val bannerResponse = novelApiService.getBannerNovels()
                if (bannerResponse.success && bannerResponse.data != null) {
                    val novels = bannerResponse.data.map { NovelMapper.mapDtoToDomain(it) }
                    val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                    novelDao.insertNovels(entities)
                    allNovelIds.addAll(novels.map { it.id })
                }
            } catch (e: Exception) {
                android.util.Log.e("NovelRepository", "Error fetching banner novels", e)
            }
            
            // Fetch recommended novels
            try {
                val recommendedResponse = novelApiService.getRecommendedNovels()
                if (recommendedResponse.success && recommendedResponse.data != null) {
                    val novels = recommendedResponse.data.map { NovelMapper.mapDtoToDomain(it) }
                    val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                    novelDao.insertNovels(entities)
                    allNovelIds.addAll(novels.map { it.id })
                }
            } catch (e: Exception) {
                android.util.Log.e("NovelRepository", "Error fetching recommended novels", e)
            }
            
            // Fetch ranking novels
            try {
                val rankingResponse = novelApiService.getRankingNovels()
                if (rankingResponse.success && rankingResponse.data != null) {
                    val novels = rankingResponse.data.map { NovelMapper.mapDtoToDomain(it) }
                    val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                    novelDao.insertNovels(entities)
                    allNovelIds.addAll(novels.map { it.id })
                }
            } catch (e: Exception) {
                android.util.Log.e("NovelRepository", "Error fetching ranking novels", e)
            }
            
            // Fetch new novels
            try {
                val newResponse = novelApiService.getNewNovels()
                if (newResponse.success && newResponse.data != null) {
                    val novels = newResponse.data.map { NovelMapper.mapDtoToDomain(it) }
                    val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                    novelDao.insertNovels(entities)
                    allNovelIds.addAll(novels.map { it.id })
                }
            } catch (e: Exception) {
                android.util.Log.e("NovelRepository", "Error fetching new novels", e)
            }
            
            // Fetch completed novels
            try {
                val completedResponse = novelApiService.getCompletedNovels()
                if (completedResponse.success && completedResponse.data != null) {
                    val novels = completedResponse.data.content.map { NovelMapper.mapDtoToDomain(it) }
                    val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                    novelDao.insertNovels(entities)
                    allNovelIds.addAll(novels.map { it.id })
                }
            } catch (e: Exception) {
                android.util.Log.e("NovelRepository", "Error fetching completed novels", e)
            }
            
            android.util.Log.d("NovelRepository", "Cache cleared and refreshed with ${allNovelIds.size} novels")
        } catch (e: Exception) {
            android.util.Log.e("NovelRepository", "Error clearing cache and refreshing", e)
            throw e
        }
    }

    // Legacy implementations (kept for backward compatibility)
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
