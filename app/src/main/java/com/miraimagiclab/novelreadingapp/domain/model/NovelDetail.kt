package com.miraimagiclab.novelreadingapp.domain.model

data class NovelDetail(
    val novel: Novel,
    val chapters: List<Chapter>,
    val reviews: List<Review>,
    val comments: List<Comment>,
    val recommendations: List<Novel>
)
