package com.miraimagiclab.novelreadingapp.dto.request

import com.miraimagiclab.novelreadingapp.enumeration.EntityEnum
import com.miraimagiclab.novelreadingapp.enumeration.NotificationEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class NotificationCreateRequest(
    @field:NotBlank val userId: String,
    @field:NotNull val type: NotificationEnum,
    @field:NotBlank val title: String,
    @field:NotBlank val message: String,
    val entityId: String? = null,
    val entityType: EntityEnum? = null,
    val actorId: String? = null,
    val actorName: String? = null,
    val contextData: String? = null,
    val link: String? = null
)
