package com.miraimagiclab.novelreadingapp.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import java.time.LocalDateTime

@Document(collection = "device_tokens")
data class DeviceToken(
    @Id
    val id: String? = null,
    
    @Indexed
    @Field(targetType = FieldType.OBJECT_ID)
    val userId: String,
    
    @Indexed
    val token: String, // FCM token
    
    val deviceType: String = "android", // android, ios
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

