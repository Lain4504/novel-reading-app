package com.miraimagiclab.novelreadingapp.util

import android.util.Base64
import org.json.JSONObject
import java.util.Date

object JwtTokenHelper {
    
    /**
     * Checks if the JWT token is expired
     * @param token JWT token string
     * @return true if token is expired, false otherwise
     */
    fun isTokenExpired(token: String): Boolean {
        return try {
            val expirationTime = getExpirationTime(token)
            expirationTime.before(Date())
        } catch (e: Exception) {
            true // If we can't parse the token, consider it expired
        }
    }
    
    /**
     * Checks if the JWT token will expire within the specified threshold
     * @param token JWT token string
     * @param thresholdMinutes Number of minutes before expiration to consider "expiring soon"
     * @return true if token expires within threshold, false otherwise
     */
    fun isTokenExpiringSoon(token: String, thresholdMinutes: Int): Boolean {
        return try {
            val expirationTime = getExpirationTime(token)
            val thresholdTime = Date(System.currentTimeMillis() + (thresholdMinutes * 60 * 1000))
            expirationTime.before(thresholdTime)
        } catch (e: Exception) {
            true // If we can't parse the token, consider it expired
        }
    }
    
    /**
     * Extracts the expiration time from a JWT token
     * @param token JWT token string
     * @return Date object representing the expiration time
     * @throws Exception if token cannot be parsed
     */
    fun getExpirationTime(token: String): Date {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token format")
        }
        
        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
        val payloadJson = String(decodedBytes)
        val jsonObject = JSONObject(payloadJson)
        
        val exp = jsonObject.getLong("exp")
        return Date(exp * 1000) // Convert from seconds to milliseconds
    }
    
    /**
     * Extracts the user ID from a JWT token
     * @param token JWT token string
     * @return User ID as string
     * @throws Exception if token cannot be parsed
     */
    fun getUserId(token: String): String {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token format")
        }
        
        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
        val payloadJson = String(decodedBytes)
        val jsonObject = JSONObject(payloadJson)
        
        return jsonObject.getString("userId")
    }
    
    /**
     * Extracts the username from a JWT token
     * @param token JWT token string
     * @return Username as string
     * @throws Exception if token cannot be parsed
     */
    fun getUsername(token: String): String {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token format")
        }
        
        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
        val payloadJson = String(decodedBytes)
        val jsonObject = JSONObject(payloadJson)
        
        return jsonObject.getString("username")
    }
}
