package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ImageRequest(
    @field:NotBlank val originalFilename: String,
    @field:NotBlank val contentType: String,
    val fileSize: Long,
    @field:NotBlank val storageKey: String,
    @field:NotBlank val ownerId: String,
    @field:NotBlank val ownerType: String
)