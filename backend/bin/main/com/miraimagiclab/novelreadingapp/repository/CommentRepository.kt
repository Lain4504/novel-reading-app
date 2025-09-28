package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.model.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : MongoRepository<Comment, String> {
    fun findByNovelId(novelId: String, pageable: Pageable): Page<Comment>
    fun findByParentId(parentId: String, pageable: Pageable): Page<Comment>
    fun countByNovelId(novelId: String): Long
    fun existsByIdAndDeletedFalse(id: String): Boolean
}
