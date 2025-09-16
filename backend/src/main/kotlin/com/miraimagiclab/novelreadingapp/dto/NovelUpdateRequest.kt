package com.miraimagiclab.novelreadingapp.dto

import com.miraimagiclab.novelreadingapp.model.CategoryEnum
import com.miraimagiclab.novelreadingapp.model.NovelStatusEnum
import jakarta.validation.constraints.*

data class NovelUpdateRequest(
    @field:Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    val title: String? = null,
    
    @field:Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    val description: String? = null,
    
    @field:Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
    val authorName: String? = null,
    
    val coverImage: String? = null,
    
    val categories: Set<CategoryEnum>? = null,
    
    @field:Size(min = 0, max = 5, message = "Rating must be between 0 and 5")
    val rating: Double? = null,
    
    val wordCount: Int? = null,
    
    val chapterCount: Int? = null,
    
    val authorId: String? = null,
    
    val status: NovelStatusEnum? = null,
    
    val isR18: Boolean? = null
)
