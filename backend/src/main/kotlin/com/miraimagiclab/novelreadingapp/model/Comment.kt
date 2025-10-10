package com.miraimagiclab.novelreadingapp.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import com.miraimagiclab.novelreadingapp.enumeration.CommentEnum
import java.time.LocalDateTime

@Document(collection = "comments")
data class Comment(
    @Id val id: String? = null,
    val content: String,
    @Field(targetType = FieldType.OBJECT_ID)
    val userId: String,
    val targetType: CommentEnum,
    @Field(targetType = FieldType.OBJECT_ID)
    val novelId: String? = null,
    @Field(targetType = FieldType.OBJECT_ID)
    val parentId: String? = null,
    val level: Int? = 1,
    @Field(targetType = FieldType.OBJECT_ID)
    val replyToId: String? = null,
    val replyToUserName: String? = null,
    val likeCount: Int? = 0,
    val replyCount: Int? = 0,
    val deleted: Boolean = false,
    val message: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)