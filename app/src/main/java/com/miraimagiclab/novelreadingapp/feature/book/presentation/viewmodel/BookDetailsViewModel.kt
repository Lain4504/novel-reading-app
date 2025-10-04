package com.miraimagiclab.novelreadingapp.feature.book.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import com.miraimagiclab.novelreadingapp.feature.book.domain.entity.BookDetail
import com.miraimagiclab.novelreadingapp.feature.book.domain.usecase.GetBookDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase
) : ViewModel() {

    private val _bookDetail = MutableStateFlow<BookDetailState>(BookDetailState.Loading)
    val bookDetail: StateFlow<BookDetailState> = _bookDetail.asStateFlow()

    fun loadBookDetail(bookId: String) {
        viewModelScope.launch {
            getBookDetailUseCase(bookId).collect { result ->
                _bookDetail.value = when (result) {
                    is ApiResult.Loading -> BookDetailState.Loading
                    is ApiResult.Success -> BookDetailState.Success(result.data)
                    is ApiResult.Error -> BookDetailState.Error(result.message)
                }
            }
        }
    }
}

sealed class BookDetailState {
    object Loading : BookDetailState()
    data class Success(val data: BookDetail) : BookDetailState()
    data class Error(val message: String) : BookDetailState()
}
