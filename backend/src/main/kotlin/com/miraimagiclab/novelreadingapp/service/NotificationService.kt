package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.dto.response.NotificationResponseDto
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.enumeration.EntityEnum
import com.miraimagiclab.novelreadingapp.enumeration.NotificationEnum
import com.miraimagiclab.novelreadingapp.exception.NotificationNotFoundException
import com.miraimagiclab.novelreadingapp.model.Notification
import com.miraimagiclab.novelreadingapp.repository.NotificationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {
    
    // Tạo notification mới
    fun createNotification(
        userId: String,
        type: NotificationEnum,
        title: String,
        message: String,
        entityId: String? = null,
        entityType: EntityEnum? = null
    ): NotificationResponseDto {
        val notification = Notification(
            userId = userId,
            type = type,
            title = title,
            message = message,
            read = false,
            entityId = entityId,
            entityType = entityType,
            createdAt = LocalDateTime.now()
        )
        val saved = notificationRepository.save(notification)
        return NotificationResponseDto.fromEntity(saved)
    }

    // Lấy notification theo ID
    fun getNotificationById(id: String): NotificationResponseDto {
        val notification = notificationRepository.findById(id)
            .orElseThrow { NotificationNotFoundException(id) }
        return NotificationResponseDto.fromEntity(notification)
    }

    // Xóa notification theo ID
    fun deleteNotification(id: String) {
        if (!notificationRepository.existsById(id)) {
            throw NotificationNotFoundException(id)
        }
        notificationRepository.deleteById(id)
    }

    // Lấy danh sách notification của user theo page/size
    fun getNotificationsByUser(userId: String, page: Int, size: Int): PageResponse<NotificationResponseDto> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val notifications = notificationRepository.findByUserId(userId, pageable)
        val dtoPage = notifications.map { NotificationResponseDto.fromEntity(it) }
        return PageResponse.fromPage(dtoPage)
    }

    // Đếm số notification chưa đọc
    fun countUnreadNotifications(userId: String): Long {
        return notificationRepository.countByUserIdAndReadFalse(userId)
    }

    // Đánh dấu 1 notification là đã đọc
    fun markAsRead(id: String): NotificationResponseDto {
        val notification = notificationRepository.findById(id)
            .orElseThrow { NotificationNotFoundException(id) }

        val updated = notification.copy(
            read = true
        )
        val saved = notificationRepository.save(updated)
        return NotificationResponseDto.fromEntity(saved)
    }

    // Đánh dấu tất cả notification của user là đã đọc
    fun markAllAsRead(userId: String) {
        val all = notificationRepository.findByUserId(userId)
        val updatedList = all.map { it.copy(read = true) }
        notificationRepository.saveAll(updatedList)
    }
}
