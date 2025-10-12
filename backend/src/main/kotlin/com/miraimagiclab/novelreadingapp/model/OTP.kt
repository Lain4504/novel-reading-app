package com.miraimagiclab.novelreadingapp.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "otps")
data class OTP(
    @Id
    val id: String? = null,
    val email: String,
    val code: String,
    val type: OTPType,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime,
    var isUsed: Boolean = false
)

enum class OTPType {
    PASSWORD_RESET,
    ACCOUNT_VERIFICATION
}
