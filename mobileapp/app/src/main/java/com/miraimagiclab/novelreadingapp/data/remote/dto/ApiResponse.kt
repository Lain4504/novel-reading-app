package com.miraimagiclab.novelreadingapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: T? = null,
    
    @SerializedName("timestamp")
    val timestamp: String,
    
    @SerializedName("errors")
    val errors: List<String>? = null
)
