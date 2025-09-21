package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.model.Chapter
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ChapterRepository : MongoRepository<Chapter, String> {
    fun existsByNovelId(novelId: String): Boolean
    fun countChapterByNovelId(novelId: String) : Int
}