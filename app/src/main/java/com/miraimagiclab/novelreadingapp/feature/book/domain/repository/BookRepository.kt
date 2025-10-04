package com.miraimagiclab.novelreadingapp.feature.book.domain.repository

import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import com.miraimagiclab.novelreadingapp.feature.book.domain.entity.BookDetail
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getBookDetail(bookId: String): Flow<ApiResult<BookDetail>>
}
