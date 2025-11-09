package com.miraimagiclab.novelreadingapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RecommendedNovelDto(
    @SerializedName("novel")
    val novel: NovelDto,
    @SerializedName("score")
    val score: Double,
    @SerializedName("reason")
    val reason: String
)