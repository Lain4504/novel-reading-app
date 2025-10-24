package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.remote.api.CommentApiService
import com.miraimagiclab.novelreadingapp.domain.model.Comment
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val commentApiService: CommentApiService
) : ViewModel() {

    private val _commentsState = MutableStateFlow<UiState<List<Comment>>>(UiState.Idle)
    val commentsState: StateFlow<UiState<List<Comment>>> = _commentsState.asStateFlow()

    fun loadComments(novelId: String) {
        viewModelScope.launch {
            try {
                _commentsState.value = UiState.Loading
                
                val response = commentApiService.getCommentsByNovel(novelId)
                if (response.success && response.data != null) {
                    // Convert DTOs to domain models
                    val comments = response.data!!.content.map { dto ->
                        Comment(
                            id = dto.id,
                            content = dto.content,
                            userId = dto.userId,
                            targetType = dto.targetType,
                            novelId = dto.novelId,
                            parentId = dto.parentId,
                            level = dto.level,
                            replyToId = dto.replyToId,
                            replyToUserName = dto.replyToUserName,
                            likeCount = dto.likeCount,
                            replyCount = dto.replyCount,
                            createdAt = dto.createdAt,
                            updatedAt = dto.updatedAt
                        )
                    }
                    _commentsState.value = UiState.Success(comments)
                } else {
                    _commentsState.value = UiState.Error("Failed to load comments: ${response.message}")
                }
            } catch (e: Exception) {
                _commentsState.value = UiState.Error("Failed to load comments: ${e.message}")
            }
        }
    }

    fun addComment(novelId: String, content: String) {
        viewModelScope.launch {
            try {
                // TODO: Implement add comment API call
                // For now, just reload comments
                loadComments(novelId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun likeComment(commentId: String) {
        viewModelScope.launch {
            try {
                // TODO: Implement like comment API call
                // For now, just reload comments
                val currentState = _commentsState.value
                if (currentState is UiState.Success) {
                    // Optimistically update like count
                    val updatedComments = currentState.data.map { comment ->
                        if (comment.id == commentId) {
                            comment.copy(likeCount = comment.likeCount + 1)
                        } else {
                            comment
                        }
                    }
                    _commentsState.value = UiState.Success(updatedComments)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
