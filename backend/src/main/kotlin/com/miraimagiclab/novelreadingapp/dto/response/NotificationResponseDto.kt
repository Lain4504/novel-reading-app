package com.miraimagiclab.novelreadingapp.dto.response

import com.miraimagiclab.novelreadingapp.enumeration.EntityEnum
import com.miraimagiclab.novelreadingapp.enumeration.NotificationEnum
import com.miraimagiclab.novelreadingapp.model.Notification
import java.time.LocalDateTime

data class NotificationResponseDto(
    val id: String?,
    val userId: String,
    val type: NotificationEnum,
    val title: String,
    val message: String,
    val read: Boolean,
    val entityId: String?,
    val entityType: EntityEnum?,
    val actorId: String?,
    val actorName: String?,
    val contextData: String?,
    val link: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(entity: Notification): NotificationResponseDto {
            return NotificationResponseDto(
                id = entity.id,
                userId = entity.userId,
                type = entity.type,
                title = entity.title,
                message = entity.message,
                read = entity.read,
                entityId = entity.entityId,
                entityType = entity.entityType,
                actorId = entity.actorId,
                actorName = entity.actorName,
                contextData = entity.contextData,
                link = entity.link,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
