package com.miraimagiclab.novelreadingapp.dto.response

import com.miraimagiclab.novelreadingapp.dto.response.NovelDto

data class RecommendedNovelDto(
    val novel: NovelDto,
    val score: Double,
    val reason: String
)