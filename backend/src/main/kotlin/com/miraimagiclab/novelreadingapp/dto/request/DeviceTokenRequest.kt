package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.NotBlank

data class DeviceTokenRequest(
    @field:NotBlank(message = "Token is required")
    val token: String,
    
    val deviceType: String = "android"
)

