package com.miraimagiclab.novelreadingapp.util

import com.miraimagiclab.novelreadingapp.data.Book
import com.miraimagiclab.novelreadingapp.data.BookType
import com.miraimagiclab.novelreadingapp.domain.model.Novel

object NovelToBookConverter {
    
    fun novelToBook(novel: Novel): Book {
        return Book(
            id = novel.id,
            title = novel.title,
            author = novel.authorName,
            type = BookType.NOVEL, // Default to NOVEL for all novels
            genres = novel.categories,
            score = (novel.rating * 10).toInt(), // Convert rating to score (0-10 to 0-100)
            coverUrl = novel.coverImage ?: "https://via.placeholder.com/150x200?text=No+Cover",
            readTime = calculateReadTime(novel.wordCount),
            releaseDate = novel.createdAt,
            isCompleted = novel.status.name == "COMPLETED"
        )
    }

    private fun calculateReadTime(wordCount: Int): String {
        // Assuming average reading speed of 200 words per minute
        val minutes = wordCount / 200
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        
        return if (hours > 0) {
            "${hours} hours ${remainingMinutes} minutes"
        } else {
            "${minutes} minutes"
        }
    }
}
