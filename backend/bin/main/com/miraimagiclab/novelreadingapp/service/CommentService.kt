package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.model.Comment
import com.miraimagiclab.novelreadingapp.repository.CommentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository
) {
    fun create(novelId: String, comment: Comment): Comment {
        val newComment = comment.copy(novelId = novelId)
        return commentRepository.save(newComment)
    }

    fun getById(id: String): Comment =
        commentRepository.findById(id).orElseThrow { RuntimeException("Comment not found") }

    fun getByNovelId(novelId: String, pageable: Pageable): Page<Comment> =
        commentRepository.findByNovelId(novelId, pageable)

    fun update(id: String, comment: Comment): Comment {
        val existing = getById(id)
        val updated = existing.copy(
            content = comment.content,
            updatedAt = comment.updatedAt
        )
        return commentRepository.save(updated)
    }

    fun delete(id: String) {
        if (!commentRepository.existsById(id)) {
            throw RuntimeException("Comment not found")
        }
        commentRepository.deleteById(id)
    }
}
