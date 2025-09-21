package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.model.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ReviewRepository : MongoRepository<Review, String> {

    fun findByUserId(userId: String): List<Review>

    fun findByUserId(userId: String, pageable: Pageable): Page<Review>

    fun findByNovelId(novelId: String): List<Review>

    fun findByNovelId(novelId: String, pageable: Pageable): Page<Review>

    fun findByUserIdAndNovelId(userId: String, novelId: String): Optional<Review>

    fun countByNovelId(novelId: String): Long

    fun countByUserId(userId: String): Long

    fun existsByUserIdAndNovelId(userId: String, novelId: String): Boolean

    fun findTop10ByNovelIdOrderByCreatedAtDesc(novelId: String): List<Review>

    fun findByNovelIdOrderByCreatedAtDesc(novelId: String, pageable: Pageable): Page<Review>
}