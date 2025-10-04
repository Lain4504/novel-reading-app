package com.miraimagiclab.novelreadingapp.feature.home.data.model

import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.UserStats as DomainUserStats

data class UserStats(
    val totalBooksRead: Int,
    val totalReadingTime: Long, // in minutes
    val currentStreak: Int,
    val favoriteGenre: String?
) {
    fun toDomain(): DomainUserStats {
        return DomainUserStats(
            totalBooksRead = totalBooksRead,
            totalReadingTime = totalReadingTime,
            currentStreak = currentStreak,
            favoriteGenre = favoriteGenre
        )
    }
}
