package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ChapterRequestDto(
    @field:NotNull(message = "Novel ID is required")
    val novelId: String,

    @field:NotNull(message = "Chapter title is required")
    @field:Size(min = 1, max = 200, message = "Chapter title must be between 1 and 200 characters")
    val chapterTitle: String,

    @field:NotNull(message = "Chapter content is required")
    val content: String
)
