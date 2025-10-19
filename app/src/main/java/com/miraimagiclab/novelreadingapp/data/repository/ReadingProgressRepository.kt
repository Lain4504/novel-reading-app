package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.mapper.ReadingProgressMapper
import com.miraimagiclab.novelreadingapp.data.remote.api.UserNovelInteractionApiService
import com.miraimagiclab.novelreadingapp.domain.model.ReadingProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadingProgressRepository @Inject constructor(
    private val userNovelInteractionApiService: UserNovelInteractionApiService
) {
    
    suspend fun getReadingProgress(userId: String, novelId: String): ReadingProgress? {
        return try {
            val response = userNovelInteractionApiService.getUserNovelInteraction(userId, novelId)
            if (response.success && response.data != null) {
                ReadingProgressMapper.mapDtoToDomain(response.data)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun getReadingProgressFlow(userId: String, novelId: String): Flow<ReadingProgress?> = flow {
        emit(getReadingProgress(userId, novelId))
    }
    
    suspend fun updateReadingProgress(userId: String, novelId: String, chapterId: String, chapterNumber: Int): ReadingProgress? {
        return try {
            val response = userNovelInteractionApiService.updateReadingProgress(
                userId = userId,
                novelId = novelId,
                chapterNumber = chapterNumber,
                chapterId = chapterId
            )
            if (response.success && response.data != null) {
                ReadingProgressMapper.mapDtoToDomain(response.data)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
