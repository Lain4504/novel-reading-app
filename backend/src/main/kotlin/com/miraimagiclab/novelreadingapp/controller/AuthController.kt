package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.dto.ForgotPasswordRequest
import com.miraimagiclab.novelreadingapp.dto.ResetPasswordRequest
import com.miraimagiclab.novelreadingapp.dto.VerifyOTPRequest
import com.miraimagiclab.novelreadingapp.enumeration.UserStatusEnum
import com.miraimagiclab.novelreadingapp.model.OTPType
import com.miraimagiclab.novelreadingapp.service.OTPService
import com.miraimagiclab.novelreadingapp.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and account management")
class AuthController(
    private val otpService: OTPService,
    private val userService: UserService
) {
    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestBody request: ForgotPasswordRequest): ResponseEntity<ApiResponse<Unit>> {
        val user = userService.findByEmail(request.email)
            ?: return ResponseEntity.ok(ApiResponse.error("User not found"))

        otpService.generateOTP(request.email, OTPType.PASSWORD_RESET)
        return ResponseEntity.ok(ApiResponse.success(message = "Password reset OTP sent successfully"))
    }

    @PostMapping("/verify-reset-otp")
    fun verifyResetOTP(@RequestBody request: VerifyOTPRequest): ResponseEntity<ApiResponse<Boolean>> {
        val isValid = otpService.verifyOTP(request.email, request.code, OTPType.PASSWORD_RESET)
        return if (isValid) {
            ResponseEntity.ok(ApiResponse.success(true, "OTP verified successfully"))
        } else {
            ResponseEntity.ok(ApiResponse.error("Invalid or expired OTP"))
        }
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody request: ResetPasswordRequest): ResponseEntity<ApiResponse<Unit>> {
        val isValid = otpService.verifyOTP(request.email, request.code, OTPType.PASSWORD_RESET)
        if (!isValid) {
            return ResponseEntity.ok(ApiResponse.error("Invalid or expired OTP"))
        }

        userService.resetPassword(request.email, request.newPassword)
        return ResponseEntity.ok(ApiResponse.success(message = "Password reset successfully"))
    }

    @PostMapping("/verify-account")
    fun verifyAccount(@RequestBody request: VerifyOTPRequest): ResponseEntity<ApiResponse<Boolean>> {
        val isValid = otpService.verifyOTP(request.email, request.code, OTPType.ACCOUNT_VERIFICATION)
        if (isValid) {
            userService.activateAccount(request.email)
            return ResponseEntity.ok(ApiResponse.success(true, "Account verified successfully"))
        }
        return ResponseEntity.ok(ApiResponse.error("Invalid or expired OTP"))
    }

    @PostMapping("/resend-verification")
    fun resendVerification(@RequestParam email: String): ResponseEntity<ApiResponse<Unit>> {
        val user = userService.findByEmail(email)
            ?: return ResponseEntity.ok(ApiResponse.error("User not found"))

        if (user.status == UserStatusEnum.ACTIVE) {
            return ResponseEntity.ok(ApiResponse.error("Account is already verified"))
        }

        otpService.generateOTP(email, OTPType.ACCOUNT_VERIFICATION)
        return ResponseEntity.ok(ApiResponse.success(message = "Verification OTP sent successfully"))
    }
}
