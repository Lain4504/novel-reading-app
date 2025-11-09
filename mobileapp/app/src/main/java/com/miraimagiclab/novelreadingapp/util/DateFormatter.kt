package com.miraimagiclab.novelreadingapp.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateFormatter {
    
    /**
     * Formats a date string to a user-friendly relative time format
     * Examples: "Just now", "5m ago", "2h ago", "3d ago", "Jan 15, 2024"
     * 
     * @param dateString The date string in ISO_DATE_TIME format (e.g., "2024-01-15T10:30:00")
     * @return Formatted date string that is user-friendly
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatRelativeDate(dateString: String): String {
        return try {
            // Try different date formats
            val dateTime = parseDate(dateString) ?: return dateString
            val now = LocalDateTime.now()
            val diff = java.time.Duration.between(dateTime, now)

            formatDuration(diff, dateTime)
        } catch (e: Exception) {
            // Fallback to original string if parsing fails
            dateString
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDate(dateString: String): LocalDateTime? {
        // Try ISO_DATE_TIME format first (e.g., "2024-01-15T10:30:00" or "2024-01-15T10:30:00.123")
        return try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: DateTimeParseException) {
            // Try ISO_LOCAL_DATE_TIME format as fallback (e.g., "2024-01-15T10:30:00")
            try {
                LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } catch (e2: DateTimeParseException) {
                null
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDuration(diff: java.time.Duration, dateTime: LocalDateTime): String {
        return when {
            diff.toMinutes() < 1 -> "Just now"
            diff.toMinutes() < 60 -> "${diff.toMinutes()}m ago"
            diff.toHours() < 24 -> "${diff.toHours()}h ago"
            diff.toDays() < 7 -> "${diff.toDays()}d ago"
            diff.toDays() < 30 -> {
                val weeks = (diff.toDays() / 7).toInt()
                "${weeks}w ago"
            }
            diff.toDays() < 365 -> {
                val months = (diff.toDays() / 30).toInt()
                "${months}mo ago"
            }
            else -> {
                val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                dateTime.format(dateFormatter)
            }
        }
    }
}

