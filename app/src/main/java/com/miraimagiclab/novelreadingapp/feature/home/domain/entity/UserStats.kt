package com.miraimagiclab.novelreadingapp.feature.home.domain.entity

data class UserStats(
    val totalBooksRead: Int,
    val totalReadingTime: Long, // in minutes
    val currentStreak: Int,
    val favoriteGenre: String?
)
