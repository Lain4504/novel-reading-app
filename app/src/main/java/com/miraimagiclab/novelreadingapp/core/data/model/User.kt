package com.miraimagiclab.novelreadingapp.core.data.model

/**
 * Shared User models for all features
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val joinDate: String,
    val isVerified: Boolean = false
)

data class UserStats(
    val totalBooksRead: Int,
    val totalReadingTime: Long, // in minutes
    val currentStreak: Int,
    val favoriteGenre: String?,
    val bookPoints: Int = 0
)
