package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import com.miraimagiclab.novelreadingapp.enumeration.NotificationEnum
import com.miraimagiclab.novelreadingapp.enumeration.EntityEnum

data class NotificationRequest(
    @field:NotBlank val userId: String,
    val type: NotificationEnum,
    @field:NotBlank val title: String,
    @field:NotBlank val message: String,
    val entityId: String? = null,
    val entityType: EntityEnum? = null,
    val actorId: String? = null,
    val actorName: String? = null,
    val contextData: String? = null,
    val link: String? = null
)