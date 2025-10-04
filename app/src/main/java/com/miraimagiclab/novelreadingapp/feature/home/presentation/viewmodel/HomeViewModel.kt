package com.miraimagiclab.novelreadingapp.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.core.utils.ApiResult
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.Book
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.FeaturedBook
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.PickBook
import com.miraimagiclab.novelreadingapp.feature.home.domain.entity.UserStats
import com.miraimagiclab.novelreadingapp.core.data.repository.BookRepository
import com.miraimagiclab.novelreadingapp.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    // User Stats State
    private val _userStats = MutableStateFlow<UserStatsState>(UserStatsState.Loading)
    val userStats: StateFlow<UserStatsState> = _userStats.asStateFlow()

    // Featured Book State
    private val _featuredBook = MutableStateFlow<FeaturedBookState>(FeaturedBookState.Loading)
    val featuredBook: StateFlow<FeaturedBookState> = _featuredBook.asStateFlow()

    // Recommended Books State
    private val _recommendedBooks = MutableStateFlow<BooksState>(BooksState.Loading)
    val recommendedBooks: StateFlow<BooksState> = _recommendedBooks.asStateFlow()

    // Our Picks State
    private val _ourPicks = MutableStateFlow<PickBooksState>(PickBooksState.Loading)
    val ourPicks: StateFlow<PickBooksState> = _ourPicks.asStateFlow()

    // Loading State for refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        fetchAllData()
    }

    private fun fetchAllData() {
        viewModelScope.launch {
            // Fetch all data in parallel for better performance
            coroutineScope {
                val userStatsDeferred = async { fetchUserStats() }
                val featuredBookDeferred = async { fetchFeaturedBook() }
                val recommendedBooksDeferred = async { fetchRecommendedBooks() }
                val ourPicksDeferred = async { fetchOurPicks() }

                // Wait for all to complete
                userStatsDeferred.await()
                featuredBookDeferred.await()
                recommendedBooksDeferred.await()
                ourPicksDeferred.await()
            }
        }
    }

    fun refreshData() {
        _isRefreshing.value = true
        fetchAllData()
        _isRefreshing.value = false
    }

    private suspend fun fetchUserStats() {
        userRepository.getUserStats().collect { result ->
            _userStats.value = when (result) {
                is ApiResult.Loading -> UserStatsState.Loading
                is ApiResult.Success -> UserStatsState.Success(result.data)
                is ApiResult.Error -> UserStatsState.Error(result.message)
            }
        }
    }

    private suspend fun fetchFeaturedBook() {
        bookRepository.getFeaturedBooks().collect { result ->
            _featuredBook.value = when (result) {
                is ApiResult.Loading -> FeaturedBookState.Loading
                is ApiResult.Success -> FeaturedBookState.Success(result.data.firstOrNull()?.let { 
                    FeaturedBook(
                        id = it.id,
                        title = it.title,
                        author = it.author,
                        coverImageUrl = it.coverUrl,
                        description = it.description,
                        genre = it.genres.firstOrNull(),
                        rating = it.rating,
                        totalChapters = null,
                        isCompleted = it.isCompleted,
                        featuredReason = "Featured"
                    )
                })
                is ApiResult.Error -> FeaturedBookState.Error(result.message)
            }
        }
    }

    private suspend fun fetchRecommendedBooks() {
        bookRepository.getRecommendedBooks().collect { result ->
            _recommendedBooks.value = when (result) {
                is ApiResult.Loading -> BooksState.Loading
                is ApiResult.Success -> BooksState.Success(result.data.map { 
                    Book(
                        id = it.id,
                        title = it.title,
                        author = it.author,
                        coverImageUrl = it.coverUrl,
                        description = it.description,
                        genre = it.genres.firstOrNull(),
                        rating = it.rating,
                        totalChapters = null,
                        isCompleted = it.isCompleted
                    )
                })
                is ApiResult.Error -> BooksState.Error(result.message)
            }
        }
    }

    private suspend fun fetchOurPicks() {
        bookRepository.getOurPicks().collect { result ->
            _ourPicks.value = when (result) {
                is ApiResult.Loading -> PickBooksState.Loading
                is ApiResult.Success -> PickBooksState.Success(result.data.map { 
                    PickBook(
                        id = it.id,
                        title = it.title,
                        author = it.author,
                        coverImageUrl = it.coverUrl,
                        description = it.description,
                        genre = it.genres.firstOrNull(),
                        rating = it.rating,
                        totalChapters = null,
                        isCompleted = it.isCompleted,
                        pickReason = "Editor's Pick"
                    )
                })
                is ApiResult.Error -> PickBooksState.Error(result.message)
            }
        }
    }
}

// Sealed classes for different UI states
sealed class UserStatsState {
    object Loading : UserStatsState()
    data class Success(val data: UserStats) : UserStatsState()
    data class Error(val message: String) : UserStatsState()
}

sealed class FeaturedBookState {
    object Loading : FeaturedBookState()
    data class Success(val data: FeaturedBook) : FeaturedBookState()
    data class Error(val message: String) : FeaturedBookState()
}

sealed class BooksState {
    object Loading : BooksState()
    data class Success(val data: List<Book>) : BooksState()
    data class Error(val message: String) : BooksState()
}

sealed class PickBooksState {
    object Loading : PickBooksState()
    data class Success(val data: List<PickBook>) : PickBooksState()
    data class Error(val message: String) : PickBooksState()
}
