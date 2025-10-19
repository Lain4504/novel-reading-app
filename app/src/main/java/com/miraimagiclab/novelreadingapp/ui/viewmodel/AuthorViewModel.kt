package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.auth.AuthState
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.data.repository.AuthorRepository
import com.miraimagiclab.novelreadingapp.data.repository.UserRepository
import com.miraimagiclab.novelreadingapp.data.remote.dto.ChapterDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.NovelDto
import com.miraimagiclab.novelreadingapp.data.remote.dto.PageResponse
import com.miraimagiclab.novelreadingapp.data.remote.dto.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthorViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authorRepository: AuthorRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthorUiState())
    val uiState: StateFlow<AuthorUiState> = _uiState.asStateFlow()
    
    val authState: StateFlow<AuthState> = sessionManager.authState

    private val _authorNovels = MutableStateFlow<List<NovelDto>>(emptyList())
    val authorNovels: StateFlow<List<NovelDto>> = _authorNovels.asStateFlow()

    private val _novelChapters = MutableStateFlow<List<ChapterDto>>(emptyList())
    val novelChapters: StateFlow<List<ChapterDto>> = _novelChapters.asStateFlow()

    fun requestAuthorRole(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.requestAuthorRole(userId)
                .onSuccess { user ->
                    // Update session with new roles
                    sessionManager.saveSession(
                        accessToken = sessionManager.authState.value.accessToken!!,
                        refreshToken = sessionManager.authState.value.refreshToken!!,
                        userId = user.id,
                        username = user.username,
                        email = user.email,
                        roles = user.roles.toSet(),
                        expirationTime = sessionManager.authState.value.tokenExpiration
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthor = true,
                        currentUser = user
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun loadAuthorNovels(authorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authorRepository.getNovelsByAuthor(authorId)
                .onSuccess { pageResponse ->
                    _authorNovels.value = pageResponse.content
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun createNovel(
        title: String,
        description: String,
        authorName: String,
        authorId: String?,
        categories: Set<String>,
        status: String = "DRAFT",
        isR18: Boolean = false,
        coverImageFile: File? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authorRepository.createNovel(
                title = title,
                description = description,
                authorName = authorName,
                authorId = authorId,
                categories = categories,
                status = status,
                isR18 = isR18,
                coverImageFile = coverImageFile
            )
                .onSuccess { novel ->
                    _authorNovels.value = _authorNovels.value + novel
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        createdNovel = novel
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun updateNovel(
        novelId: String,
        title: String? = null,
        description: String? = null,
        authorName: String? = null,
        authorId: String? = null,
        categories: Set<String>? = null,
        rating: Double? = null,
        wordCount: Int? = null,
        chapterCount: Int? = null,
        status: String? = null,
        isR18: Boolean? = null,
        coverImageFile: File? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authorRepository.updateNovel(
                novelId = novelId,
                title = title,
                description = description,
                authorName = authorName,
                authorId = authorId,
                categories = categories,
                rating = rating,
                wordCount = wordCount,
                chapterCount = chapterCount,
                status = status,
                isR18 = isR18,
                coverImageFile = coverImageFile
            )
                .onSuccess { updatedNovel ->
                    _authorNovels.value = _authorNovels.value.map { novel ->
                        if (novel.id == novelId) updatedNovel else novel
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        updatedNovel = updatedNovel
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun deleteNovel(novelId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authorRepository.deleteNovel(novelId)
                .onSuccess {
                    _authorNovels.value = _authorNovels.value.filter { it.id != novelId }
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun loadNovelChapters(novelId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authorRepository.getChaptersByNovel(novelId)
                .onSuccess { pageResponse ->
                    _novelChapters.value = pageResponse.content
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun createChapter(
        novelId: String,
        chapterTitle: String,
        chapterNumber: Int,
        content: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authorRepository.createChapter(
                novelId = novelId,
                chapterTitle = chapterTitle,
                chapterNumber = chapterNumber,
                content = content
            )
                .onSuccess { chapter ->
                    _novelChapters.value = _novelChapters.value + chapter
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        createdChapter = chapter
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun updateChapter(
        novelId: String,
        chapterId: String,
        chapterTitle: String? = null,
        chapterNumber: Int? = null,
        content: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authorRepository.updateChapter(
                novelId = novelId,
                chapterId = chapterId,
                chapterTitle = chapterTitle,
                chapterNumber = chapterNumber,
                content = content
            )
                .onSuccess { updatedChapter ->
                    _novelChapters.value = _novelChapters.value.map { chapter ->
                        if (chapter.id == chapterId) updatedChapter else chapter
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        updatedChapter = updatedChapter
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun deleteChapter(chapterId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authorRepository.deleteChapter(chapterId)
                .onSuccess {
                    _novelChapters.value = _novelChapters.value.filter { it.id != chapterId }
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearCreatedNovel() {
        _uiState.value = _uiState.value.copy(createdNovel = null)
    }

    fun clearUpdatedNovel() {
        _uiState.value = _uiState.value.copy(updatedNovel = null)
    }

    fun clearCreatedChapter() {
        _uiState.value = _uiState.value.copy(createdChapter = null)
    }

    fun clearUpdatedChapter() {
        _uiState.value = _uiState.value.copy(updatedChapter = null)
    }
}

data class AuthorUiState(
    val isLoading: Boolean = false,
    val isAuthor: Boolean = false,
    val currentUser: UserDto? = null,
    val error: String? = null,
    val createdNovel: NovelDto? = null,
    val updatedNovel: NovelDto? = null,
    val createdChapter: ChapterDto? = null,
    val updatedChapter: ChapterDto? = null
)
