package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.model.Notification
import com.miraimagiclab.novelreadingapp.repository.NotificationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {
    fun create(userId: String, notification: Notification): Notification {
        val toSave = notification.copy(
            userId = userId,
            createdAt = LocalDateTime.now(),
            read = false
        )
        return notificationRepository.save(toSave)
    }

    fun getById(id: String): Notification =
        notificationRepository.findById(id)
            .orElseThrow { RuntimeException("Notification not found") }

    fun getByUserId(userId: String, pageable: Pageable): Page<Notification> =
        notificationRepository.findByUserId(userId, pageable)

    fun countUnread(userId: String): Long =
        notificationRepository.countByUserIdAndReadFalse(userId)

    fun markAsRead(id: String) {
        val notification = getById(id)
        val updated = notification.copy(
            read = true,
            updatedAt = LocalDateTime.now()
        )
        notificationRepository.save(updated)
    }
}
