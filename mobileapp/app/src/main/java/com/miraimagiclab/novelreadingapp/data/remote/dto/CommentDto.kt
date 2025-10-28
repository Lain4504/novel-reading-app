package com.miraimagiclab.novelreadingapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("username")
    val username: String?,
    
    @SerializedName("avatarUrl")
    val avatarUrl: String?,
    
    @SerializedName("targetType")
    val targetType: String,
    
    @SerializedName("novelId")
    val novelId: String?,
    
    @SerializedName("parentId")
    val parentId: String?,
    
    @SerializedName("level")
    val level: Int,
    
    @SerializedName("replyToId")
    val replyToId: String?,
    
    @SerializedName("replyToUserName")
    val replyToUserName: String?,
    
    @SerializedName("likeCount")
    val likeCount: Int,
    
    @SerializedName("replyCount")
    val replyCount: Int,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String
)


