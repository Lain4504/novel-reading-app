package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.domain.model.Comment
import com.miraimagiclab.novelreadingapp.ui.components.novel.CommentItem
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing
import com.miraimagiclab.novelreadingapp.ui.viewmodel.CommentsViewModel
import com.miraimagiclab.novelreadingapp.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    novelId: String,
    onBackClick: () -> Unit,
    onAvatarClick: (String) -> Unit = {},
    viewModel: CommentsViewModel = hiltViewModel()
) {
    var commentText by remember { mutableStateOf("") }
    var replyToComment by remember { mutableStateOf<Comment?>(null) }
    
    // Load comments when screen opens
    LaunchedEffect(novelId) {
        viewModel.loadComments(novelId)
    }
    
    val commentsState by viewModel.commentsState.collectAsState()
    val repliesState by viewModel.repliesState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Comments",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = WindowInsets(0)
            )
        },
        bottomBar = {
            CommentInputBar(
                commentText = commentText,
                onCommentTextChange = { commentText = it },
                replyToComment = replyToComment,
                onSendClick = {
                    if (commentText.isNotBlank()) {
                        if (replyToComment != null) {
                            // Send reply
                            viewModel.addReply(
                                commentId = replyToComment!!.id,
                                content = commentText,
                                replyToUserName = replyToComment!!.userId // Use the comment author's userId as username
                            )
                            replyToComment = null
                        } else {
                            // Send regular comment
                            viewModel.addComment(novelId, commentText)
                        }
                        commentText = ""
                    }
                },
                onCancelReply = {
                    replyToComment = null
                }
            )
        }
    ) { innerPadding ->
        when (val state = commentsState) {
            is UiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No comments yet")
                }
            }
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Text(
                            text = "Failed to load comments",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        TextButton(onClick = { viewModel.loadComments(novelId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is UiState.Success -> {
                val comments = state.data
                if (comments.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "No comments",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No comments yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Be the first to comment!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
            items(comments) { comment ->
                // Only show main comments (level 1 or no parentId)
                if (comment.level == 1 || comment.parentId == null) {
                    val commentRepliesState = repliesState[comment.id]
                    val showReplies = commentRepliesState != null
                    val replies = if (commentRepliesState is UiState.Success) commentRepliesState.data else emptyList()
                    val isLoadingReplies = commentRepliesState is UiState.Loading
                    
                    CommentItem(
                        comment = comment,
                        onReplyClick = {
                            replyToComment = comment
                        },
                        onShowRepliesClick = { commentId ->
                            viewModel.loadReplies(commentId)
                        },
                        onHideRepliesClick = { commentId ->
                            viewModel.clearReplies(commentId)
                        },
                        onAvatarClick = onAvatarClick,
                        showReplies = showReplies,
                        replies = replies,
                        isLoadingReplies = isLoadingReplies
                    )
                }
            }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentInputBar(
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    replyToComment: Comment?,
    onSendClick: () -> Unit,
    onCancelReply: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column {
            // Reply indicator bar
            if (replyToComment != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Replying to @${replyToComment.username ?: replyToComment.userId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = onCancelReply,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel reply",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            
            // Input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = onCommentTextChange,
                    placeholder = { 
                        Text(
                            if (replyToComment != null) "Write a reply..." else "Thêm bình luận..."
                        ) 
                    },
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
                
                IconButton(
                    onClick = onSendClick,
                    enabled = commentText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = if (replyToComment != null) "Send reply" else "Send comment",
                        tint = if (commentText.isNotBlank()) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
