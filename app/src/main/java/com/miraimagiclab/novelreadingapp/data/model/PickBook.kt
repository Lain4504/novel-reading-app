package com.miraimagiclab.novelreadingapp.data.model

import com.google.gson.annotations.SerializedName

data class PickBook(
    val id: String,
    val title: String,
    val author: String,
    @SerializedName("cover_url")
    val coverUrl: String
)
