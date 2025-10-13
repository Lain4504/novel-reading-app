package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.dto.response.NotificationResponseDto
import com.miraimagiclab.novelreadingapp.service.NotificationService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notifications")
@CrossOrigin(
    origins = [
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:8080",
        "http://127.0.0.1:8080"
    ]
)
@Tag(name = "Notification Management", description = "APIs for managing user notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @GetMapping("/{id}")
    fun getNotificationById(
        @PathVariable id: String
    ): ResponseEntity<ApiResponse<NotificationResponseDto>> {
        val notification = notificationService.getNotificationById(id)
        return ResponseEntity.ok(ApiResponse.success(notification, "Notification retrieved successfully"))
    }

    @GetMapping("/users/{userId}")
    fun getNotificationsByUserId(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PageResponse<NotificationResponseDto>>> {
        val notifications = notificationService.getNotificationsByUser(userId, page, size)
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications retrieved successfully"))
    }

    @PostMapping("/{id}/read")
    fun markAsRead(
        @PathVariable id: String
    ): ResponseEntity<ApiResponse<NotificationResponseDto>> {
        val updated = notificationService.markAsRead(id)
        return ResponseEntity.ok(ApiResponse.success(updated, "Notification marked as read"))
    }

    @DeleteMapping("/{id}")
    fun deleteNotification(
        @PathVariable id: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        notificationService.deleteNotification(id)
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully"))
    }
}
