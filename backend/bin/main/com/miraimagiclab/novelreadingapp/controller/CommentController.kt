package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.model.Comment
import com.miraimagiclab.novelreadingapp.service.CommentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping("/novels/{novelId}")
    fun createComment(
        @PathVariable novelId: String,
        @RequestBody comment: Comment
    ): ResponseEntity<ApiResponse<Comment>> {
        val saved = commentService.create(novelId, comment)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(saved, "Comment created successfully"))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<ApiResponse<Comment>> {
        val comment = commentService.getById(id)
        return ResponseEntity.ok(ApiResponse.success(comment, "Comment retrieved successfully"))
    }

    @GetMapping("/novels/{novelId}")
    fun getByNovelId(
        @PathVariable novelId: String,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<Comment>>> {
        val comments = commentService.getByNovelId(novelId, pageable)
        return ResponseEntity.ok(ApiResponse.success(comments, "Comments retrieved successfully"))
    }

    @PutMapping("/{id}")
    fun updateComment(
        @PathVariable id: String,
        @RequestBody comment: Comment
    ): ResponseEntity<ApiResponse<Comment>> {
        val updated = commentService.update(id, comment)
        return ResponseEntity.ok(ApiResponse.success(updated, "Comment updated successfully"))
    }

    @DeleteMapping("/{id}")
    fun deleteComment(@PathVariable id: String): ResponseEntity<ApiResponse<Unit>> {
        commentService.delete(id)
        return ResponseEntity.ok(ApiResponse.success(message = "Comment deleted successfully"))
    }
}
