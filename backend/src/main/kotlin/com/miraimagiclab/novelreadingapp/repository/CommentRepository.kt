package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.model.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : MongoRepository<Comment, String> {

    fun findByNovelIdAndParentIdIsNullAndDeletedFalse(novelId: String, pageable: Pageable): Page<Comment>

    fun findByParentIdAndDeletedFalse(parentId: String, pageable: Pageable): Page<Comment>

    fun countByNovelIdAndDeletedFalse(novelId: String): Long

    fun countByParentIdAndDeletedFalse(parentId: String): Long
}
