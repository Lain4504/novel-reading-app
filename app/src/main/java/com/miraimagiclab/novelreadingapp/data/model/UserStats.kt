package com.miraimagiclab.novelreadingapp.data.model

import com.google.gson.annotations.SerializedName

data class UserStats(
    @SerializedName("book_points")
    val bookPoints: Int,
    @SerializedName("read_books")
    val readBooks: Int,
    @SerializedName("user_name")
    val userName: String
)
