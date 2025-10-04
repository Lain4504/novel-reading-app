package com.miraimagiclab.novelreadingapp.data.model

import com.google.gson.annotations.SerializedName

data class FeaturedBook(
    val title: String,
    @SerializedName("cover_url")
    val coverUrl: String,
    val series: String
)
