package com.miraimagiclab.novelreadingapp.dto.response

data class ImageResponse(
    val id: String,
    val url: String,
    val ownerId: String,
    val ownerType: String,
    val originalFilename: String? = null,
    val contentType: String? = null,
    val fileSize: Long? = null,
    val active: Boolean? = null
)
