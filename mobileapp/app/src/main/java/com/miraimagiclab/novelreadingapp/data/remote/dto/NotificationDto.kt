package com.miraimagiclab.novelreadingapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    @SerializedName("id")
    val id: String?,
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("type")
    val type: String, // NotificationEnum as string
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("read")
    val read: Boolean,
    
    @SerializedName("entityId")
    val entityId: String?, // ID của entity liên quan (novelId, commentId, etc.)
    
    @SerializedName("entityType")
    val entityType: String?, // EntityEnum as string (NOVEL, COMMENT, etc.)
    
    @SerializedName("createdAt")
    val createdAt: String
)

