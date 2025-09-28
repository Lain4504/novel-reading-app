package com.miraimagiclab.novelreadingapp.dto.response

import java.time.LocalDateTime

data class ImageResponseDto(
    val id: String,
    val originalFilename: String,
    val contentType: String,
    val fileSize: Long,
    val storageKey: String,
    val ownerId: String,
    val ownerType: String,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)