package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.dto.request.CommentCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.CommentReplyCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.CommentUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.CommentResponseDto
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.exception.CommentNotFoundException
import com.miraimagiclab.novelreadingapp.model.Comment
import com.miraimagiclab.novelreadingapp.repository.CommentRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CommentService(
    private val commentRepository: CommentRepository
) {

    fun createComment(request: CommentCreateRequest): CommentResponseDto {
        val comment = Comment(
            novelId = request.novelId,
            userId = request.userId,
            content = request.content,
            targetType = request.targetType,
            parentId = null,
            level = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            deleted = false
        )

        val saved = commentRepository.save(comment)
        return CommentResponseDto.fromEntity(saved)
    }

    fun createReply(commentId: String, request: CommentReplyCreateRequest): CommentResponseDto {
        val parent = commentRepository.findById(commentId)
            .orElseThrow { CommentNotFoundException("Parent comment with ID '$commentId' not found") }

        val reply = Comment(
            novelId = parent.novelId,
            userId = request.userId,
            content = request.content,
            targetType = parent.targetType,
            parentId = parent.id,
            replyToId = parent.userId,
            replyToUserName = request.replyToUserName,
            level = (parent.level ?: 1) + 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            deleted = false
        )

        val saved = commentRepository.save(reply)
        
        // Update parent comment's reply count
        val updatedParent = parent.copy(
            replyCount = (parent.replyCount ?: 0) + 1,
            updatedAt = LocalDateTime.now()
        )
        commentRepository.save(updatedParent)
        
        return CommentResponseDto.fromEntity(saved)
    }

    @Transactional(readOnly = true)
    fun getCommentById(id: String): CommentResponseDto {
        val comment = commentRepository.findById(id)
            .orElseThrow { CommentNotFoundException("Comment with ID '$id' not found") }
        return CommentResponseDto.fromEntity(comment)
    }

    @Transactional(readOnly = true)
    fun getCommentsByNovelId(novelId: String, page: Int = 0, size: Int = 20): PageResponse<CommentResponseDto> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val comments = commentRepository.findByNovelIdAndParentIdIsNullAndDeletedFalse(novelId, pageable)
        val commentDtos = comments.content.map { CommentResponseDto.fromEntity(it) }

        return PageResponse(
            content = commentDtos,
            page = comments.number,
            size = comments.size,
            totalElements = comments.totalElements,
            totalPages = comments.totalPages,
            first = comments.isFirst,
            last = comments.isLast,
            numberOfElements = comments.numberOfElements
        )
    }

    @Transactional(readOnly = true)
    fun getRepliesByCommentId(commentId: String, page: Int = 0, size: Int = 10): PageResponse<CommentResponseDto> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"))
        val replies = commentRepository.findByParentIdAndDeletedFalse(commentId, pageable)
        val replyDtos = replies.content.map { CommentResponseDto.fromEntity(it) }

        return PageResponse(
            content = replyDtos,
            page = replies.number,
            size = replies.size,
            totalElements = replies.totalElements,
            totalPages = replies.totalPages,
            first = replies.isFirst,
            last = replies.isLast,
            numberOfElements = replies.numberOfElements
        )
    }

    fun updateComment(id: String, request: CommentUpdateRequest): CommentResponseDto {
        val existing = commentRepository.findById(id)
            .orElseThrow { CommentNotFoundException("Comment with ID '$id' not found") }

        val updated = existing.copy(
            content = request.content ?: existing.content,
            updatedAt = LocalDateTime.now()
        )

        val saved = commentRepository.save(updated)
        return CommentResponseDto.fromEntity(saved)
    }

    fun deleteComment(id: String) {
        val comment = commentRepository.findById(id)
            .orElseThrow { CommentNotFoundException("Comment with ID '$id' not found") }

        val updated = comment.copy(deleted = true, updatedAt = LocalDateTime.now())
        commentRepository.save(updated)
    }
}
