package com.miraimagiclab.novelreadingapp.dto.request

data class NotificationUpdateRequest(
    val title: String? = null,
    val message: String? = null,
    val read: Boolean? = null
)
