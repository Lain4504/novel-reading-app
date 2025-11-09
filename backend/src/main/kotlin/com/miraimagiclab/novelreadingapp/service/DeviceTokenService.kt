package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.dto.request.DeviceTokenRequest
import com.miraimagiclab.novelreadingapp.model.DeviceToken
import com.miraimagiclab.novelreadingapp.repository.DeviceTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class DeviceTokenService(
    private val deviceTokenRepository: DeviceTokenRepository
) {
    
    fun saveOrUpdateToken(userId: String, request: DeviceTokenRequest): DeviceToken {
        val logger = org.slf4j.LoggerFactory.getLogger(DeviceTokenService::class.java)
        logger.info("=== DeviceTokenService: Saving or updating token ===")
        logger.info("UserId: $userId")
        logger.info("Token (first 20 chars): ${request.token.take(20)}...")
        logger.info("DeviceType: ${request.deviceType}")
        
        val existingToken = deviceTokenRepository.findByUserIdAndToken(userId, request.token)
        
        return if (existingToken.isPresent) {
            // Update existing token
            logger.info("Token already exists. Updating timestamp...")
            val token = existingToken.get()
            val updatedToken = token.copy(
                updatedAt = LocalDateTime.now()
            ).let { deviceTokenRepository.save(it) }
            logger.info("✓ Token updated successfully for user $userId")
            updatedToken
        } else {
            // Create new token
            logger.info("Creating new token for user $userId")
            val newToken = DeviceToken(
                userId = userId,
                token = request.token,
                deviceType = request.deviceType,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            val savedToken = deviceTokenRepository.save(newToken)
            logger.info("✓ New token saved successfully for user $userId")
            logger.info("=== DeviceTokenService: Token saved ===")
            savedToken
        }
    }
    
    fun getUserTokens(userId: String): List<DeviceToken> {
        return deviceTokenRepository.findByUserId(userId)
    }
    
    fun deleteToken(userId: String, token: String) {
        deviceTokenRepository.findByUserIdAndToken(userId, token)
            .ifPresent { deviceTokenRepository.delete(it) }
    }
    
    fun deleteAllUserTokens(userId: String) {
        deviceTokenRepository.deleteByUserId(userId)
    }
    
    fun getTokensByUserIds(userIds: List<String>): Map<String, List<String>> {
        val logger = org.slf4j.LoggerFactory.getLogger(DeviceTokenService::class.java)
        logger.info("=== DeviceTokenService: Getting tokens for user IDs ===")
        logger.info("Requested user IDs: ${userIds.size} - $userIds")
        
        val tokensByUserId = mutableMapOf<String, List<String>>()
        userIds.forEach { userId ->
            val tokens = deviceTokenRepository.findByUserId(userId)
            logger.info("User $userId: Found ${tokens.size} token(s) in database")
            
            if (tokens.isNotEmpty()) {
                val tokenStrings = tokens.map { it.token }
                tokensByUserId[userId] = tokenStrings
                tokenStrings.forEachIndexed { index, token ->
                    logger.debug("User $userId token ${index + 1}: ${token.take(20)}... (deviceType: ${tokens[index].deviceType})")
                }
            } else {
                logger.warn("User $userId: No tokens found in database")
            }
        }
        
        logger.info("Total users with tokens: ${tokensByUserId.size} out of ${userIds.size}")
        logger.info("=== DeviceTokenService: Token retrieval completed ===")
        return tokensByUserId
    }
}

