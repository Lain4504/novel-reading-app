package com.miraimagiclab.novelreadingapp.model

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import com.miraimagiclab.novelreadingapp.enumeration.NotificationEnum
import com.miraimagiclab.novelreadingapp.enumeration.EntityEnum
import java.time.LocalDateTime

@Document(collection = "notifications")
data class Notification(
    @Id val id: String? = null,
    @Field(targetType = FieldType.OBJECT_ID)
    @Indexed
    val userId: String,
    @Indexed
    val type: NotificationEnum,
    val title: String,
    val message: String,
    val read: Boolean = false,
    @Field(targetType = FieldType.OBJECT_ID)
    val entityId: String? = null,
    val entityType: EntityEnum? = null,
    @Field(targetType = FieldType.OBJECT_ID)
    val actorId: String? = null,
    val actorName: String? = null,
    val contextData: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val link: String? = null
)