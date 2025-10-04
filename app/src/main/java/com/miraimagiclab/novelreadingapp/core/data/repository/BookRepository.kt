package com.miraimagiclab.novelreadingapp.core.data.repository

import com.miraimagiclab.novelreadingapp.core.data.api.ApiService
import com.miraimagiclab.novelreadingapp.core.data.model.Book
import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shared Book repository for all book-related operations
 * Used by home, book details, search, library features
 */
@Singleton
class BookRepository @Inject constructor(
    apiService: ApiService
) : BaseRepository(apiService) {

    fun getFeaturedBooks(): Flow<ApiResult<List<Book>>> = safeApiCall {
        apiService.getFeaturedNovel()
    }

    fun getRecommendedBooks(page: Int = 0, size: Int = 10): Flow<ApiResult<List<Book>>> = safeApiCall {
        apiService.getRecommendedBooks(page, size)
    }

    fun getOurPicks(category: String = "novel", limit: Int = 10): Flow<ApiResult<List<Book>>> = safeApiCall {
        apiService.getOurPicks(category, limit)
    }

    fun searchBooks(query: String, page: Int = 0, size: Int = 10): Flow<ApiResult<List<Book>>> = safeApiCall {
        // TODO: Add search endpoint to ApiService
        apiService.getRecommendedBooks(page, size) // Temporary
    }

    fun getBookById(bookId: String): Flow<ApiResult<Book>> = safeApiCall {
        // TODO: Add get book by ID endpoint
        apiService.getRecommendedBooks(0, 1) // Temporary
    }
}
