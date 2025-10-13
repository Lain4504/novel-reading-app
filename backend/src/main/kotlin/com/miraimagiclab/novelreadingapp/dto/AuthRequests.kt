package com.miraimagiclab.novelreadingapp.dto

data class ForgotPasswordRequest(
    val email: String
)

data class VerifyOTPRequest(
    val email: String,
    val code: String
)

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)
