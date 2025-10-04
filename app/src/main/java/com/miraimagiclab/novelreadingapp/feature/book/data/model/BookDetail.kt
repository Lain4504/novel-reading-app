package com.miraimagiclab.novelreadingapp.feature.book.data.model

import com.miraimagiclab.novelreadingapp.feature.book.domain.entity.BookDetail as DomainBookDetail
import com.miraimagiclab.novelreadingapp.feature.book.domain.entity.Book as DomainBook
import com.miraimagiclab.novelreadingapp.feature.book.domain.entity.Chapter as DomainChapter
import com.miraimagiclab.novelreadingapp.feature.book.domain.entity.Volume as DomainVolume
import com.miraimagiclab.novelreadingapp.feature.book.domain.entity.Review as DomainReview
import com.miraimagiclab.novelreadingapp.feature.book.domain.entity.BookType as DomainBookType

data class BookDetail(
    val book: Book,
    val summary: String,
    val chapters: List<Chapter>,
    val volumes: List<Volume>,
    val reviews: List<Review>,
    val recommendations: List<Book>
) {
    fun toDomain(): DomainBookDetail {
        return DomainBookDetail(
            book = book.toDomain(),
            summary = summary,
            chapters = chapters.map { it.toDomain() },
            volumes = volumes.map { it.toDomain() },
            reviews = reviews.map { it.toDomain() },
            recommendations = recommendations.map { it.toDomain() }
        )
    }
}

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val series: String? = null,
    val type: BookType,
    val genres: List<String>,
    val score: Int,
    val coverUrl: String,
    val readTime: String,
    val releaseDate: String,
    val isCompleted: Boolean = false
) {
    fun toDomain(): DomainBook {
        return DomainBook(
            id = id,
            title = title,
            author = author,
            series = series,
            type = when (type) {
                BookType.NOVEL -> DomainBookType.NOVEL
                BookType.LIGHT_NOVEL -> DomainBookType.LIGHT_NOVEL
                BookType.MANGA -> DomainBookType.MANGA
            },
            genres = genres,
            score = score,
            coverUrl = coverUrl,
            readTime = readTime,
            releaseDate = releaseDate,
            isCompleted = isCompleted
        )
    }
}

data class Chapter(
    val id: String,
    val title: String,
    val isUnlocked: Boolean = true,
    val thumbnailUrl: String? = null
) {
    fun toDomain(): DomainChapter {
        return DomainChapter(
            id = id,
            title = title,
            isUnlocked = isUnlocked,
            thumbnailUrl = thumbnailUrl
        )
    }
}

data class Volume(
    val id: String,
    val title: String,
    val releaseDate: String,
    val coverUrl: String
) {
    fun toDomain(): DomainVolume {
        return DomainVolume(
            id = id,
            title = title,
            releaseDate = releaseDate,
            coverUrl = coverUrl
        )
    }
}

data class Review(
    val id: String,
    val username: String,
    val rating: Int,
    val comment: String,
    val date: String
) {
    fun toDomain(): DomainReview {
        return DomainReview(
            id = id,
            username = username,
            rating = rating,
            comment = comment,
            date = date
        )
    }
}

enum class BookType {
    NOVEL, LIGHT_NOVEL, MANGA
}
