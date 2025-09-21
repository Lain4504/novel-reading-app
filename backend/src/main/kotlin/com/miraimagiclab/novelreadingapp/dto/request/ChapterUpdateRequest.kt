package com.miraimagiclab.novelreadingapp.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChapterUpdateRequest(
    @field:NotBlank(message = "Chapter title is required")
    @field:Size(min = 1, max = 200, message = "Chapter title must be between 1 and 200 characters")
    val chapterTitle: String,

    @field:NotBlank(message = "Chapter content is required")
    val content: String
)
