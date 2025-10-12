package com.miraimagiclab.novelreadingapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PageResponse<T>(
    @SerializedName("content")
    val content: List<T>,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("size")
    val size: Int,
    
    @SerializedName("totalElements")
    val totalElements: Long,
    
    @SerializedName("totalPages")
    val totalPages: Int,
    
    @SerializedName("first")
    val first: Boolean,
    
    @SerializedName("last")
    val last: Boolean,
    
    @SerializedName("numberOfElements")
    val numberOfElements: Int
)
