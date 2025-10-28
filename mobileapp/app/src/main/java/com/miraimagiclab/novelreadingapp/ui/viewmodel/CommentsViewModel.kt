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
    private val commentApiService: CommentApiService,
    private val sessionManager: com.miraimagiclab.novelreadingapp.data.auth.SessionManager
) : ViewModel() {

    private val _commentsState = MutableStateFlow<UiState<List<Comment>>>(UiState.Idle)
    val commentsState: StateFlow<UiState<List<Comment>>> = _commentsState.asStateFlow()

    // State for managing replies for each comment
    private val _repliesState = MutableStateFlow<Map<String, UiState<List<Comment>>>>(emptyMap())
    val repliesState: StateFlow<Map<String, UiState<List<Comment>>>> = _repliesState.asStateFlow()

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
                            username = dto.username,
                            avatarUrl = dto.avatarUrl,
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
                val currentUser = sessionManager.authState.value
                if (!currentUser.isLoggedIn || currentUser.userId == null) {
                    _commentsState.value = UiState.Error("User not logged in")
                    return@launch
                }
                
                val request = com.miraimagiclab.novelreadingapp.data.remote.dto.request.CommentCreateRequestDto(
                    content = content,
                    targetType = "NOVEL",
                    userId = currentUser.userId,
                    novelId = novelId
                )
                
                val response = commentApiService.createComment(request)
                if (response.success && response.data != null) {
                    // Optimistically add the new comment to the current comments list
                    val currentState = _commentsState.value
                    if (currentState is UiState.Success) {
                        val newComment = Comment(
                            id = response.data!!.id,
                            content = response.data!!.content,
                            userId = response.data!!.userId,
                            username = response.data!!.username,
                            avatarUrl = response.data!!.avatarUrl,
                            targetType = response.data!!.targetType,
                            novelId = response.data!!.novelId,
                            parentId = response.data!!.parentId,
                            level = response.data!!.level,
                            replyToId = response.data!!.replyToId,
                            replyToUserName = response.data!!.replyToUserName,
                            likeCount = response.data!!.likeCount,
                            replyCount = response.data!!.replyCount,
                            createdAt = response.data!!.createdAt,
                            updatedAt = response.data!!.updatedAt
                        )
                        
                        // Add the new comment to the existing comments
                        val updatedComments = currentState.data + newComment
                        _commentsState.value = UiState.Success(updatedComments)
                    } else {
                        // If current state is not Success, reload comments to get fresh data
                        loadComments(novelId)
                    }
                } else {
                    _commentsState.value = UiState.Error("Failed to create comment: ${response.message}")
                }
            } catch (e: Exception) {
                _commentsState.value = UiState.Error("Failed to create comment: ${e.message}")
            }
        }
    }

    fun addReply(commentId: String, content: String, replyToUserName: String) {
        viewModelScope.launch {
            try {
                val currentUser = sessionManager.authState.value
                if (!currentUser.isLoggedIn || currentUser.userId == null) {
                    _commentsState.value = UiState.Error("User not logged in")
                    return@launch
                }
                
                val request = com.miraimagiclab.novelreadingapp.data.remote.dto.request.CommentReplyCreateRequestDto(
                    content = content,
                    userId = currentUser.userId,
                    replyToUserName = replyToUserName
                )
                
                val response = commentApiService.createReply(commentId, request)
                if (response.success && response.data != null) {
                    val currentState = _commentsState.value
                    if (currentState is UiState.Success) {
                        // Update parent comment's replyCount
                        val updatedComments = currentState.data.map { comment ->
                            if (comment.id == commentId) {
                                comment.copy(replyCount = comment.replyCount + 1)
                            } else {
                                comment
                            }
                        }
                        _commentsState.value = UiState.Success(updatedComments)
                        
                        // If replies are currently shown, add new reply to replies list
                        val currentRepliesState = _repliesState.value[commentId]
                        if (currentRepliesState is UiState.Success) {
                            val newReply = Comment(
                                id = response.data!!.id,
                                content = response.data!!.content,
                                userId = response.data!!.userId,
                                username = response.data!!.username,
                                avatarUrl = response.data!!.avatarUrl,
                                targetType = response.data!!.targetType,
                                novelId = response.data!!.novelId,
                                parentId = response.data!!.parentId,
                                level = response.data!!.level,
                                replyToId = response.data!!.replyToId,
                                replyToUserName = response.data!!.replyToUserName,
                                likeCount = response.data!!.likeCount,
                                replyCount = response.data!!.replyCount,
                                createdAt = response.data!!.createdAt,
                                updatedAt = response.data!!.updatedAt
                            )
                            val updatedReplies = currentRepliesState.data + newReply
                            val updatedRepliesMap = _repliesState.value.toMutableMap()
                            updatedRepliesMap[commentId] = UiState.Success(updatedReplies)
                            _repliesState.value = updatedRepliesMap
                        }
                    }
                } else {
                    _commentsState.value = UiState.Error("Failed to create reply: ${response.message}")
                }
            } catch (e: Exception) {
                _commentsState.value = UiState.Error("Failed to create reply: ${e.message}")
            }
        }
    }

    fun loadReplies(commentId: String) {
        viewModelScope.launch {
            try {
                // Set loading state for this comment's replies
                val currentRepliesState = _repliesState.value.toMutableMap()
                currentRepliesState[commentId] = UiState.Loading
                _repliesState.value = currentRepliesState

                val response = commentApiService.getRepliesByCommentId(commentId)
                if (response.success && response.data != null) {
                    // Convert DTOs to domain models
                    val replies = response.data!!.content.map { dto ->
                        Comment(
                            id = dto.id,
                            content = dto.content,
                            userId = dto.userId,
                            username = dto.username,
                            avatarUrl = dto.avatarUrl,
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
                    
                    // Update replies state
                    val updatedRepliesState = _repliesState.value.toMutableMap()
                    updatedRepliesState[commentId] = UiState.Success(replies)
                    _repliesState.value = updatedRepliesState
                } else {
                    // Set error state for this comment's replies
                    val updatedRepliesState = _repliesState.value.toMutableMap()
                    updatedRepliesState[commentId] = UiState.Error("Failed to load replies: ${response.message}")
                    _repliesState.value = updatedRepliesState
                }
            } catch (e: Exception) {
                // Set error state for this comment's replies
                val updatedRepliesState = _repliesState.value.toMutableMap()
                updatedRepliesState[commentId] = UiState.Error("Failed to load replies: ${e.message}")
                _repliesState.value = updatedRepliesState
            }
        }
    }

    fun clearReplies(commentId: String) {
        val updatedRepliesState = _repliesState.value.toMutableMap()
        updatedRepliesState.remove(commentId)
        _repliesState.value = updatedRepliesState
    }

}
