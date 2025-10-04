package com.miraimagiclab.novelreadingapp.data.model

import com.google.gson.annotations.SerializedName

data class Book(
    val id: String?,
    val title: String?,
    val type: String?,
    val score: Int?,
    @SerializedName("cover_url")
    val coverUrl: String?,
    val genres: List<String>?
)
