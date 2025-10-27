package com.miraimagiclab.novelreadingapp.domain.model

import java.util.Date

data class ReadingProgress(
    val userId: String,
    val novelId: String,
    val currentChapterId: String?,
    val currentChapterNumber: Int?,
    val lastReadAt: Date?
) {
    val isCompleted: Boolean
        get() = currentChapterId != null && currentChapterNumber != null
}
