package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.config.JwtUtil
import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.dto.request.DeviceTokenRequest
import com.miraimagiclab.novelreadingapp.service.DeviceTokenService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/device-tokens")
class DeviceTokenController(
    private val deviceTokenService: DeviceTokenService,
    private val jwtUtil: JwtUtil
) {
    
    private fun getUserIdFromRequest(request: HttpServletRequest): String {
        val logger = org.slf4j.LoggerFactory.getLogger(DeviceTokenController::class.java)
        val authHeader = request.getHeader("Authorization")
        logger.info("=== DeviceTokenController: Getting userId from request ===")
        logger.debug("Authorization header present: ${authHeader != null}")
        
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            logger.debug("JWT token extracted (first 20 chars): ${token.take(20)}...")
            try {
                val userId = jwtUtil.getUserIdFromToken(token)
                logger.info("UserId extracted from token: $userId")
                logger.info("UserId length: ${userId.length}")
                // Validate userId format (should be MongoDB ObjectId - 24 hex characters)
                if (userId.length != 24 || !userId.matches(Regex("[0-9a-fA-F]{24}"))) {
                    logger.warn("⚠️ UserId format may be invalid: $userId (expected 24 hex characters)")
                }
                userId
            } catch (e: Exception) {
                logger.error("✗ Failed to extract userId from token: ${e.message}", e)
                throw IllegalStateException("Failed to extract userId from token: ${e.message}", e)
            }
        } else {
            logger.error("✗ No authorization token found in request")
            throw IllegalStateException("No authorization token found")
        }
    }
    
    @PostMapping
    fun saveDeviceToken(
        request: HttpServletRequest,
        @Valid @RequestBody deviceTokenRequest: DeviceTokenRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val logger = org.slf4j.LoggerFactory.getLogger(DeviceTokenController::class.java)
        logger.info("=== DeviceTokenController: saveDeviceToken endpoint called ===")
        
        val userId = getUserIdFromRequest(request)
        logger.info("Saving device token for userId: $userId")
        deviceTokenService.saveOrUpdateToken(userId, deviceTokenRequest)
        
        logger.info("✓ Device token saved successfully for userId: $userId")
        return ResponseEntity.ok(ApiResponse.success("Device token saved successfully"))
    }
    
    @GetMapping
    fun getUserTokens(request: HttpServletRequest): ResponseEntity<ApiResponse<List<com.miraimagiclab.novelreadingapp.model.DeviceToken>>> {
        val userId = getUserIdFromRequest(request)
        val tokens = deviceTokenService.getUserTokens(userId)
        return ResponseEntity.ok(ApiResponse.success(tokens, "Device tokens retrieved successfully"))
    }
    
    @DeleteMapping("/{token}")
    fun deleteDeviceToken(
        request: HttpServletRequest,
        @PathVariable token: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = getUserIdFromRequest(request)
        deviceTokenService.deleteToken(userId, token)
        return ResponseEntity.ok(ApiResponse.success("Device token deleted successfully"))
    }
}

