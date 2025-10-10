package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.dto.request.CommentCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.CommentReplyCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.CommentUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.CommentResponseDto
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.service.CommentService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
@CrossOrigin(
    origins = [
        "http://localhost:3000",
        "http://localhost:8080",
        "http://127.0.0.1:3000",
        "http://127.0.0.1:8080"
    ]
)
@Tag(name = "Comment Management", description = "APIs for managing comments and replies")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping
    fun createComment(
        @Valid @RequestBody request: CommentCreateRequest
    ): ResponseEntity<ApiResponse<CommentResponseDto>> {
        val comment = commentService.createComment(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(comment, "Comment created successfully"))
    }

    @PostMapping("/{commentId}/reply")
    fun createReply(
        @PathVariable commentId: String,
        @Valid @RequestBody request: CommentReplyCreateRequest
    ): ResponseEntity<ApiResponse<CommentResponseDto>> {
        val reply = commentService.createReply(commentId, request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(reply, "Reply created successfully"))
    }

    @GetMapping("/{id}")
    fun getCommentById(@PathVariable id: String): ResponseEntity<ApiResponse<CommentResponseDto>> {
        val comment = commentService.getCommentById(id)
        return ResponseEntity.ok(ApiResponse.success(comment, "Comment retrieved successfully"))
    }

    @GetMapping("/novel/{novelId}")
    fun getCommentsByNovel(
        @PathVariable novelId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PageResponse<CommentResponseDto>>> {
        val comments = commentService.getCommentsByNovelId(novelId, page, size)
        return ResponseEntity.ok(ApiResponse.success(comments, "Comments retrieved successfully"))
    }

    @GetMapping("/{commentId}/replies")
    fun getRepliesByCommentId(
        @PathVariable commentId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PageResponse<CommentResponseDto>>> {
        val replies = commentService.getRepliesByCommentId(commentId, page, size)
        return ResponseEntity.ok(ApiResponse.success(replies, "Replies retrieved successfully"))
    }

    @PutMapping("/{id}")
    fun updateComment(
        @PathVariable id: String,
        @Valid @RequestBody request: CommentUpdateRequest
    ): ResponseEntity<ApiResponse<CommentResponseDto>> {
        val updated = commentService.updateComment(id, request)
        return ResponseEntity.ok(ApiResponse.success(updated, "Comment updated successfully"))
    }

    @DeleteMapping("/{id}")
    fun deleteComment(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        commentService.deleteComment(id)
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully"))
    }
}
