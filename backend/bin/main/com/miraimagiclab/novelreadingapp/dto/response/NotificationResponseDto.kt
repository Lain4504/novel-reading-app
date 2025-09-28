package com.miraimagiclab.novelreadingapp.dto.response

import com.miraimagiclab.novelreadingapp.enumeration.NotificationEnum
import com.miraimagiclab.novelreadingapp.enumeration.EntityEnum
import java.time.LocalDateTime

data class NotificationResponseDto(
    val id: String,
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
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val link: String?
)