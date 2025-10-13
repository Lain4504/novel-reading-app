package com.miraimagiclab.novelreadingapp.dto.request

import org.springframework.web.multipart.MultipartFile

data class ImageUploadRequest(
    val file: MultipartFile,
    val ownerId: String,
    val ownerType: String
)
