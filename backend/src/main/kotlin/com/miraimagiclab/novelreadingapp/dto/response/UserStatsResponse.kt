package com.miraimagiclab.novelreadingapp.dto.response

data class UserStatsResponse(
    val bookPoints: Int = 0,
    val readBooks: Int = 0,
    val userName: String = "",
    val followingCount: Int = 0,
    val wishlistCount: Int = 0,
    val reviewsCount: Int = 0
)