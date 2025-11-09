package com.miraimagiclab.novelreadingapp.service

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.google.auth.oauth2.GoogleCredentials
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct
import java.io.FileInputStream

@Service
class FcmService {
    
    private val logger = LoggerFactory.getLogger(FcmService::class.java)
    
    @PostConstruct
    fun initializeFirebase() {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                // Initialize Firebase Admin SDK
                // You need to set GOOGLE_APPLICATION_CREDENTIALS environment variable
                // or place firebase-service-account.json in resources folder
                val serviceAccountPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
                    ?: "firebase-service-account.json"
                
                try {
                    val serviceAccount = FileInputStream(serviceAccountPath)
                    val credentials = GoogleCredentials.fromStream(serviceAccount)
                    val options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build()
                    FirebaseApp.initializeApp(options)
                    logger.info("Firebase initialized successfully")
                } catch (e: Exception) {
                    logger.warn("Firebase service account file not found. Push notifications will be disabled. Error: ${e.message}")
                    logger.warn("Please set GOOGLE_APPLICATION_CREDENTIALS environment variable or place firebase-service-account.json in the project root")
                }
            }
        } catch (e: Exception) {
            logger.error("Error initializing Firebase: ${e.message}", e)
        }
    }
    
    fun sendNotification(
        token: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): Boolean {
        return try {
            logger.info("=== FCM Service: Attempting to send notification ===")
            logger.info("Token (first 20 chars): ${token.take(20)}...")
            logger.info("Title: $title")
            logger.info("Body: $body")
            logger.info("Data: $data")
            
            if (FirebaseApp.getApps().isEmpty()) {
                logger.error("Firebase not initialized. Cannot send notification.")
                logger.error("FirebaseApp.getApps().isEmpty() = true")
                return false
            }
            
            logger.info("Firebase is initialized. Number of apps: ${FirebaseApp.getApps().size}")
            
            val notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build()
            
            logger.debug("Notification object created successfully")
            
            val messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(notification)
            
            // Always include title and body in data payload for foreground handling
            val enhancedData = data.toMutableMap()
            enhancedData["title"] = title
            enhancedData["body"] = body
            
            if (enhancedData.isNotEmpty()) {
                messageBuilder.putAllData(enhancedData)
                logger.debug("Added data payload: $enhancedData")
            }
            
            val message = messageBuilder.build()
            logger.info("Message built, sending to FCM...")
            
            val response = FirebaseMessaging.getInstance().send(message)
            logger.info("✓ Successfully sent message to FCM. Response: $response")
            logger.info("=== FCM Service: Notification sent successfully ===")
            true
        } catch (e: Exception) {
            logger.error("✗ Error sending FCM notification: ${e.message}", e)
            logger.error("Exception type: ${e.javaClass.name}")
            logger.error("Stack trace:", e)
            false
        }
    }
    
    fun sendNotificationToMultipleTokens(
        tokens: List<String>,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()
        
        tokens.forEach { token ->
            results[token] = sendNotification(token, title, body, data)
        }
        
        return results
    }
}

