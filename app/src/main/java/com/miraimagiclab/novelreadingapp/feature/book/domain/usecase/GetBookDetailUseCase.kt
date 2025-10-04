package com.miraimagiclab.novelreadingapp.feature.book.domain.usecase

import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import com.miraimagiclab.novelreadingapp.feature.book.domain.entity.BookDetail
import com.miraimagiclab.novelreadingapp.feature.book.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookDetailUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke(bookId: String): Flow<ApiResult<BookDetail>> {
        return repository.getBookDetail(bookId)
    }
}
