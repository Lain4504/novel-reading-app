package com.miraimagiclab.novelreadingapp.domain.repository

import com.miraimagiclab.novelreadingapp.domain.model.Chapter
import kotlinx.coroutines.flow.Flow

interface ChapterRepository {
    
    /**
     * Get a specific chapter by its ID
     * @param chapterId The ID of the chapter to retrieve
     * @return Flow of Chapter or null if not found
     */
    fun getChapterById(chapterId: String): Flow<Chapter?>
    
    /**
     * Get all chapters for a specific novel
     * @param novelId The ID of the novel
     * @return Flow of list of chapters
     */
    fun getChaptersByNovelId(novelId: String): Flow<List<Chapter>>
    
    /**
     * Refresh chapter data from API
     * @param chapterId The ID of the chapter to refresh
     */
    suspend fun refreshChapter(chapterId: String)
    
    /**
     * Refresh all chapters for a novel from API
     * @param novelId The ID of the novel
     */
    suspend fun refreshChaptersByNovelId(novelId: String)
    
    /**
     * Get cached chapter if available, otherwise fetch from API
     * @param chapterId The ID of the chapter
     * @return Chapter or null if not found
     */
    suspend fun getChapterByIdSync(chapterId: String): Chapter?
    
    /**
     * Get cached chapters for novel if available, otherwise fetch from API
     * @param novelId The ID of the novel
     * @return List of chapters
     */
    suspend fun getChaptersByNovelIdSync(novelId: String): List<Chapter>
    
    /**
     * Increment view count for a chapter
     * @param chapterId The ID of the chapter
     */
    suspend fun incrementViewCount(chapterId: String)
}
