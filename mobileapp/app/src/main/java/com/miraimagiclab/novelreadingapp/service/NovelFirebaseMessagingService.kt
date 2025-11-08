package com.miraimagiclab.novelreadingapp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.miraimagiclab.novelreadingapp.MainActivity
import com.miraimagiclab.novelreadingapp.util.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NovelFirebaseMessagingService : FirebaseMessagingService() {
    
    @Inject
    lateinit var fcmService: FcmService
    
    private val TAG = "NovelFirebaseMessagingService"
    
    override fun onCreate() {
        super.onCreate()
        android.util.Log.d(TAG, "=== onCreate: Initializing FCM Service ===")
        NotificationHelper.ensureChannels(this)
        android.util.Log.d(TAG, "Notification channels ensured")
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        android.util.Log.d(TAG, "=== onNewToken: FCM token refreshed ===")
        android.util.Log.d(TAG, "New token: $token")
        
        // Send token to server
        android.util.Log.d(TAG, "Sending new token to server...")
        fcmService.sendTokenToServer(token)
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        android.util.Log.d(TAG, "=== onMessageReceived: Received FCM message ===")
        android.util.Log.d(TAG, "From: ${remoteMessage.from}")
        android.util.Log.d(TAG, "Message ID: ${remoteMessage.messageId}")
        android.util.Log.d(TAG, "Message Type: ${remoteMessage.messageType}")
        android.util.Log.d(TAG, "Sent Time: ${remoteMessage.sentTime}")
        
        // Log data payload
        val data = remoteMessage.data
        android.util.Log.d(TAG, "Data payload size: ${data.size}")
        data.forEach { (key, value) ->
            android.util.Log.d(TAG, "Data[$key] = $value")
        }
        
        // Log notification payload
        val notification = remoteMessage.notification
        if (notification != null) {
            android.util.Log.d(TAG, "Notification payload present")
            android.util.Log.d(TAG, "Title: ${notification.title}")
            android.util.Log.d(TAG, "Body: ${notification.body}")
            android.util.Log.d(TAG, "Icon: ${notification.icon}")
            android.util.Log.d(TAG, "Sound: ${notification.sound}")
            android.util.Log.d(TAG, "Tag: ${notification.tag}")
        } else {
            android.util.Log.w(TAG, "⚠️ Notification payload is NULL")
            android.util.Log.w(TAG, "Message contains only data payload. This might not show as notification when app is in foreground.")
        }
        
        // Check if message contains a notification payload
        notification?.let { notif ->
            val title = notif.title ?: "Thông báo"
            val body = notif.body ?: ""
            
            // Get data payload
            val novelId = data["novelId"]
            val chapterId = data["chapterId"]
            val chapterNumber = data["chapterNumber"]
            val type = data["type"]
            
            android.util.Log.d(TAG, "Extracted notification data:")
            android.util.Log.d(TAG, "  Title: $title")
            android.util.Log.d(TAG, "  Body: $body")
            android.util.Log.d(TAG, "  Type: $type")
            android.util.Log.d(TAG, "  NovelId: $novelId")
            android.util.Log.d(TAG, "  ChapterId: $chapterId")
            android.util.Log.d(TAG, "  ChapterNumber: $chapterNumber")
            
            // Show notification
            android.util.Log.d(TAG, "Calling showNotification()...")
            showNotification(
                title = title,
                body = body,
                novelId = novelId,
                chapterId = chapterId
            )
            android.util.Log.d(TAG, "✓ Notification displayed")
        } ?: run {
            android.util.Log.w(TAG, "⚠️ No notification payload. Only data payload present.")
            android.util.Log.w(TAG, "When app is in foreground, FCM only delivers data messages.")
            android.util.Log.w(TAG, "Creating notification manually from data payload...")
            
            // Handle data-only message (app is in foreground)
            // Create notification from data payload
            val type = data["type"]
            if (type == "NEW_CHAPTER") {
                val novelId = data["novelId"]
                val chapterId = data["chapterId"]
                val chapterNumber = data["chapterNumber"]
                
                // Try to get title and body from data, or use defaults
                val title = data["title"] ?: "Có chương mới"
                val body = data["body"] ?: "Một chương mới đã được thêm vào truyện bạn theo dõi"
                
                android.util.Log.d(TAG, "Creating notification from data payload:")
                android.util.Log.d(TAG, "  Title: $title")
                android.util.Log.d(TAG, "  Body: $body")
                android.util.Log.d(TAG, "  NovelId: $novelId")
                android.util.Log.d(TAG, "  ChapterId: $chapterId")
                android.util.Log.d(TAG, "  ChapterNumber: $chapterNumber")
                
                showNotification(
                    title = title,
                    body = body,
                    novelId = novelId,
                    chapterId = chapterId
                )
                android.util.Log.d(TAG, "✓ Notification created from data payload")
            } else {
                android.util.Log.w(TAG, "Unknown message type: $type. Skipping notification creation.")
            }
        }
        
        android.util.Log.d(TAG, "=== onMessageReceived: Processing completed ===")
    }
    
    private fun showNotification(
        title: String,
        body: String,
        novelId: String?,
        chapterId: String?
    ) {
        android.util.Log.d(TAG, "=== showNotification: Creating and displaying notification ===")
        android.util.Log.d(TAG, "Title: $title")
        android.util.Log.d(TAG, "Body: $body")
        android.util.Log.d(TAG, "NovelId: $novelId")
        android.util.Log.d(TAG, "ChapterId: $chapterId")
        
        val notificationId = System.currentTimeMillis().toInt()
        android.util.Log.d(TAG, "Notification ID: $notificationId")
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add data to intent if available
            novelId?.let { 
                putExtra("novelId", it)
                android.util.Log.d(TAG, "Added novelId to intent: $it")
            }
            chapterId?.let { 
                putExtra("chapterId", it)
                android.util.Log.d(TAG, "Added chapterId to intent: $it")
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        android.util.Log.d(TAG, "PendingIntent created")
        
        val notificationBuilder = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID_NOVEL_UPDATES)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        
        android.util.Log.d(TAG, "Notification builder created with channel: ${NotificationHelper.CHANNEL_ID_NOVEL_UPDATES}")
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = notificationBuilder.build()
        notificationManager.notify(notificationId, notification)
        
        android.util.Log.d(TAG, "✓ Notification posted to notification manager with ID: $notificationId")
        android.util.Log.d(TAG, "=== showNotification: Completed ===")
    }
}

