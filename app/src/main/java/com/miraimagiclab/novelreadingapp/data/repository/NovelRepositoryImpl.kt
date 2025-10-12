package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.local.dao.NovelDao
import com.miraimagiclab.novelreadingapp.data.mapper.NovelMapper
import com.miraimagiclab.novelreadingapp.data.remote.api.NovelApiService
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.repository.NovelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NovelRepositoryImpl @Inject constructor(
    private val novelApiService: NovelApiService,
    private val novelDao: NovelDao
) : NovelRepository {

    override fun getTopNovelsByRating(): Flow<List<Novel>> {
        return novelDao.getTopNovelsByRating().map { entities ->
            entities.map { NovelMapper.mapEntityToDomain(it) }
        }
    }

    override fun getTopNovelsByFollowCount(): Flow<List<Novel>> {
        return novelDao.getTopNovelsByFollowCount().map { entities ->
            entities.map { NovelMapper.mapEntityToDomain(it) }
        }
    }

    override fun getTopNovelsByViewCount(): Flow<List<Novel>> {
        return novelDao.getTopNovelsByViewCount().map { entities ->
            entities.map { NovelMapper.mapEntityToDomain(it) }
        }
    }

    override fun getRecentlyUpdatedNovels(): Flow<List<Novel>> {
        return novelDao.getRecentlyUpdatedNovels().map { entities ->
            entities.map { NovelMapper.mapEntityToDomain(it) }
        }
    }

    override fun getCompletedNovels(): Flow<List<Novel>> {
        return novelDao.getCompletedNovels().map { entities ->
            entities.map { NovelMapper.mapEntityToDomain(it) }
        }
    }

    override suspend fun refreshTopNovelsByRating() {
        try {
            val response = novelApiService.getTopNovelsByRating()
            if (response.success && response.data != null) {
                val novels = response.data.map { NovelMapper.mapDtoToDomain(it) }
                val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                novelDao.insertNovels(entities)
            }
        } catch (e: Exception) {
            // Handle error - data will come from cache
        }
    }

    override suspend fun refreshTopNovelsByFollowCount() {
        try {
            val response = novelApiService.getTopNovelsByFollowCount()
            if (response.success && response.data != null) {
                val novels = response.data.map { NovelMapper.mapDtoToDomain(it) }
                val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                novelDao.insertNovels(entities)
            }
        } catch (e: Exception) {
            // Handle error - data will come from cache
        }
    }

    override suspend fun refreshTopNovelsByViewCount() {
        try {
            val response = novelApiService.getTopNovelsByViewCount()
            if (response.success && response.data != null) {
                val novels = response.data.map { NovelMapper.mapDtoToDomain(it) }
                val entities = novels.map { NovelMapper.mapDomainToEntity(it) }
                novelDao.insertNovels(entities)
            }
        } catch (e: Exception) {
            // Handle error - data will come from cache
        }
    }

    override suspend fun refreshRecentlyUpdatedNovels() {
        try {
            val response = novelApiService.getRecentlyUpdatedNovels()
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

    override suspend fun getNovelById(id: String): Novel? {
        return novelDao.getNovelById(id)?.let { NovelMapper.mapEntityToDomain(it) }
    }
}
