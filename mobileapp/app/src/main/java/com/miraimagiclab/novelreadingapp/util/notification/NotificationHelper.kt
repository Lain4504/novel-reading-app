package com.miraimagiclab.novelreadingapp.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.miraimagiclab.novelreadingapp.MainActivity

object NotificationHelper {

    const val CHANNEL_ID_NOVEL_UPDATES = "novel_updates_channel"
    private const val CHANNEL_NAME_NOVEL_UPDATES = "Novel Updates"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val novelUpdates = NotificationChannel(
                CHANNEL_ID_NOVEL_UPDATES,
                CHANNEL_NAME_NOVEL_UPDATES,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new chapters in followed novels"
            }
            manager.createNotificationChannel(novelUpdates)
        }
    }

    fun notifyNovelHasNewChapters(
        context: Context,
        notificationId: Int,
        novelTitle: String,
        newChaptersCount: Int
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = "$novelTitle has a new chapter"
        val message = if (newChaptersCount > 1) {
            "$newChaptersCount new chapters posted. Tap to read."
        } else {
            "1 new chapter posted. Tap to read."
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_NOVEL_UPDATES)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(notificationId, notification)
    }
}


