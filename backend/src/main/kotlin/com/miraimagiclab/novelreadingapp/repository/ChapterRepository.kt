package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.model.Chapter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ChapterRepository : MongoRepository<Chapter, String> {
    fun existsByNovelId(novelId: String): Boolean
    fun countByNovelId(novelId: String) : Int
    fun findByNovelId(novelId: String) : List<Chapter>
    fun findByNovelId(novelId: String, pageable: Pageable): Page<Chapter>
}