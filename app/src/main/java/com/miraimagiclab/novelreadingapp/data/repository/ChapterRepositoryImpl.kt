package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.mapper.ChapterMapper
import com.miraimagiclab.novelreadingapp.data.remote.api.ChapterApiService
import com.miraimagiclab.novelreadingapp.domain.model.Chapter
import com.miraimagiclab.novelreadingapp.domain.repository.ChapterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChapterRepositoryImpl @Inject constructor(
    private val chapterApiService: ChapterApiService
) : ChapterRepository {

    // In-memory cache for chapters
    private val chapterCache = mutableMapOf<String, Chapter>()
    private val novelChaptersCache = mutableMapOf<String, List<Chapter>>()

    override fun getChapterById(chapterId: String): Flow<Chapter?> = flow {
        // First try to get from cache
        val cachedChapter = chapterCache[chapterId]
        if (cachedChapter != null) {
            emit(cachedChapter)
        } else {
            // If not in cache, fetch from API
            try {
                val response = chapterApiService.getChapterById(chapterId)
                if (response.success && response.data != null) {
                    val chapter = ChapterMapper.mapDtoToDomain(response.data)
                    chapterCache[chapterId] = chapter
                    emit(chapter)
                } else {
                    emit(null)
                }
            } catch (e: Exception) {
                // Handle error - emit null for now
                emit(null)
            }
        }
    }

    override fun getChaptersByNovelId(novelId: String): Flow<List<Chapter>> = flow {
        // First try to get from cache
        val cachedChapters = novelChaptersCache[novelId]
        if (cachedChapters != null) {
            emit(cachedChapters)
        } else {
            // If not in cache, fetch from API
            try {
                val response = chapterApiService.getChaptersByNovelId(novelId)
                if (response.success && response.data != null) {
                    val chapters = response.data.content.map { ChapterMapper.mapDtoToDomain(it) }
                    novelChaptersCache[novelId] = chapters
                    // Also cache individual chapters
                    chapters.forEach { chapter ->
                        chapterCache[chapter.id] = chapter
                    }
                    emit(chapters)
                } else {
                    emit(emptyList())
                }
            } catch (e: Exception) {
                // Handle error - emit empty list for now
                emit(emptyList())
            }
        }
    }

    override suspend fun refreshChapter(chapterId: String) {
        try {
            val response = chapterApiService.getChapterById(chapterId)
            if (response.success && response.data != null) {
                val chapter = ChapterMapper.mapDtoToDomain(response.data)
                chapterCache[chapterId] = chapter
            }
        } catch (e: Exception) {
            // Handle error - could log or throw
        }
    }

    override suspend fun refreshChaptersByNovelId(novelId: String) {
        try {
            val response = chapterApiService.getChaptersByNovelId(novelId)
            if (response.success && response.data != null) {
                val chapters = response.data.content.map { ChapterMapper.mapDtoToDomain(it) }
                novelChaptersCache[novelId] = chapters
                // Also cache individual chapters
                chapters.forEach { chapter ->
                    chapterCache[chapter.id] = chapter
                }
            }
        } catch (e: Exception) {
            // Handle error - could log or throw
        }
    }

    override suspend fun getChapterByIdSync(chapterId: String): Chapter? {
        // First try cache
        val cachedChapter = chapterCache[chapterId]
        if (cachedChapter != null) {
            return cachedChapter
        }

        // If not in cache, fetch from API
        return try {
            val response = chapterApiService.getChapterById(chapterId)
            if (response.success && response.data != null) {
                val chapter = ChapterMapper.mapDtoToDomain(response.data)
                chapterCache[chapterId] = chapter
                chapter
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getChaptersByNovelIdSync(novelId: String): List<Chapter> {
        // First try cache
        val cachedChapters = novelChaptersCache[novelId]
        if (cachedChapters != null) {
            return cachedChapters
        }

        // If not in cache, fetch from API
        return try {
            val response = chapterApiService.getChaptersByNovelId(novelId)
            if (response.success && response.data != null) {
                val chapters = response.data.content.map { ChapterMapper.mapDtoToDomain(it) }
                novelChaptersCache[novelId] = chapters
                // Also cache individual chapters
                chapters.forEach { chapter ->
                    chapterCache[chapter.id] = chapter
                }
                chapters
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun incrementViewCount(chapterId: String) {
        try {
            val response = chapterApiService.incrementChapterViewCount(chapterId)
            if (response.success && response.data != null) {
                val updatedChapter = ChapterMapper.mapDtoToDomain(response.data)
                chapterCache[chapterId] = updatedChapter
            }
        } catch (e: Exception) {
            // Fire-and-forget: don't block UI on failure
            // Could log error for debugging
        }
    }
}
