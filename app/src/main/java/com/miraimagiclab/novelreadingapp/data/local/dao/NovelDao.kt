package com.miraimagiclab.novelreadingapp.data.local.dao

import androidx.room.*
import com.miraimagiclab.novelreadingapp.data.local.entity.NovelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NovelDao {

    @Query("SELECT * FROM novels ORDER BY rating DESC LIMIT :limit")
    fun getTopNovelsByRating(limit: Int = 10): Flow<List<NovelEntity>>

    @Query("SELECT * FROM novels ORDER BY followCount DESC LIMIT :limit")
    fun getTopNovelsByFollowCount(limit: Int = 10): Flow<List<NovelEntity>>

    @Query("SELECT * FROM novels ORDER BY viewCount DESC LIMIT :limit")
    fun getTopNovelsByViewCount(limit: Int = 10): Flow<List<NovelEntity>>

    @Query("SELECT * FROM novels ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentlyUpdatedNovels(limit: Int = 10): Flow<List<NovelEntity>>

    @Query("SELECT * FROM novels WHERE status = 'COMPLETED' ORDER BY updatedAt DESC LIMIT :limit")
    fun getCompletedNovels(limit: Int = 10): Flow<List<NovelEntity>>

    @Query("SELECT * FROM novels WHERE id = :id")
    suspend fun getNovelById(id: String): NovelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovels(novels: List<NovelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovel(novel: NovelEntity)

    @Query("DELETE FROM novels")
    suspend fun deleteAllNovels()

    @Query("DELETE FROM novels WHERE id = :id")
    suspend fun deleteNovelById(id: String)
}
