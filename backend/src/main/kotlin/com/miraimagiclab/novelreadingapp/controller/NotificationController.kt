package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.model.Notification
import com.miraimagiclab.novelreadingapp.service.NotificationService
import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notifications")
@CrossOrigin(
    origins = [
        "http://localhost:3000",
        "http://localhost:8080",
        "http://127.0.0.1:3000",
        "http://127.0.0.1:8080"
    ]
)
class NotificationController(
    private val notificationService: NotificationService
) {

    @PostMapping("/users/{userId}")
    fun createNotification(
        @PathVariable("userId") userId: String,
        @RequestBody notification: Notification
    ): ResponseEntity<ApiResponse<Notification>> {
        val saved = notificationService.create(userId, notification)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(saved, "Notification created successfully"))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): ResponseEntity<ApiResponse<Notification>> {
        val notification = notificationService.getById(id)
        return ResponseEntity.ok(ApiResponse.success(notification, "Notification retrieved successfully"))
    }

    @GetMapping("/users/{userId}")
    fun getByUserId(
        @PathVariable("userId") userId: String,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<Notification>>> {
        val notifications = notificationService.getByUserId(userId, pageable)
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications retrieved successfully"))
    }

    @GetMapping("/users/{userId}/unread-count")
    fun countUnread(@PathVariable("userId") userId: String): ResponseEntity<ApiResponse<Long>> {
        val count = notificationService.countUnread(userId)
        return ResponseEntity.ok(ApiResponse.success(count))
    }

    @PutMapping("/{id}/read")
    fun markAsRead(@PathVariable("id") id: String): ResponseEntity<ApiResponse<String>> {
        notificationService.markAsRead(id)
        return ResponseEntity.ok(ApiResponse.success(message = "Notification marked as read"))
    }
}
