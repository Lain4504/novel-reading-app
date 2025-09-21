package com.miraimagiclab.novelreadingapp.dto.request

import com.miraimagiclab.novelreadingapp.enumeration.CategoryEnum
import com.miraimagiclab.novelreadingapp.enumeration.NovelStatusEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Pattern

data class NovelCreateRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    val title: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    val description: String,

    @field:NotBlank(message = "Author name is required")
    @field:Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
    val authorName: String,

    @field:Size(min = 1, max = 100, message = "Author ID must be between 1 and 100 characters")
    val authorId: String? = null,

    val coverImage: String? = null,

    @field:NotEmpty(message = "At least one category is required")
    val categories: Set<CategoryEnum>,

    val status: NovelStatusEnum = NovelStatusEnum.DRAFT,

    val isR18: Boolean = false
)