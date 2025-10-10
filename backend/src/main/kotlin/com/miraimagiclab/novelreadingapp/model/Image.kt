package com.miraimagiclab.novelreadingapp.model

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import java.time.LocalDateTime

@Document(collection = "images")
data class Image(
    @Id val id: String? = null,
    val originalFilename: String,
    val contentType: String,
    val fileSize: Long,
    @Indexed
    val storageKey: String,
    @Indexed
    val ownerId: String,
    @Indexed
    val ownerType: String,
    val active: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)