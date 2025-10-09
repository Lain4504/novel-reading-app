package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.model.OTP
import com.miraimagiclab.novelreadingapp.model.OTPType
import com.miraimagiclab.novelreadingapp.repository.OTPRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class OTPService(
    private val otpRepository: OTPRepository,
    private val emailService: EmailService
) {
    companion object {
        const val OTP_LENGTH = 6
        const val OTP_EXPIRATION_MINUTES = 5L
    }

    fun generateOTP(email: String, type: OTPType): OTP {
        // Generate a random 6-digit OTP
        val code = Random.nextInt(100000, 999999).toString()
        
        // Create OTP entity
        val otp = OTP(
            email = email,
            code = code,
            type = type,
            expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES)
        )
        
        // Save to database
        val savedOTP = otpRepository.save(otp)
        
        // Send OTP via email
        when (type) {
            OTPType.PASSWORD_RESET -> sendPasswordResetOTP(email, code)
            OTPType.ACCOUNT_VERIFICATION -> sendAccountVerificationOTP(email, code)
        }
        
        return savedOTP
    }

    fun verifyOTP(email: String, code: String, type: OTPType): Boolean {
        val otp = otpRepository.findByEmailAndCodeAndType(email, code, type)
            ?: return false

        if (otp.isUsed || otp.expiresAt.isBefore(LocalDateTime.now())) {
            return false
        }

        otp.isUsed = true
        otpRepository.save(otp)
        return true
    }

    private fun sendPasswordResetOTP(email: String, code: String) {
        val subject = "Password Reset Request"
        val body = """
            Hello,
            
            You have requested to reset your password. Please use the following code to proceed:
            
            $code
            
            This code will expire in $OTP_EXPIRATION_MINUTES minutes.
            
            If you did not request this password reset, please ignore this email.
            
            Best regards,
            Novel Reading App Team
        """.trimIndent()
        
        emailService.sendEmail(email, subject, body)
    }

    private fun sendAccountVerificationOTP(email: String, code: String) {
        val subject = "Account Verification"
        val body = """
            Hello,
            
            Thank you for creating an account. Please use the following code to verify your account:
            
            $code
            
            This code will expire in $OTP_EXPIRATION_MINUTES minutes.
            
            Best regards,
            Novel Reading App Team
        """.trimIndent()
        
        emailService.sendEmail(email, subject, body)
    }
}
