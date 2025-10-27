package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,

    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, message = "New password must be at least 8 characters long")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "New password must contain at least one lowercase letter, one uppercase letter, and one number"
    )
    val newPassword: String,

    @field:NotBlank(message = "Confirm password is required")
    val confirmPassword: String
)