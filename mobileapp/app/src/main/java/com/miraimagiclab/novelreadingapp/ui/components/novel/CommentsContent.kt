package com.miraimagiclab.novelreadingapp.ui.components.novel

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraimagiclab.novelreadingapp.domain.model.*
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing

@Composable
fun CommentsContent(
    novelDetail: NovelDetail,
    onAddComment: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Comments Header with count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comments (${novelDetail.comments.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Add comment button
            IconButton(onClick = onAddComment) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add comment",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (novelDetail.comments.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.xl),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(novelDetail.comments) { comment ->
                    CommentItem(
                        comment = comment,
                        onReplyClick = { /* TODO: Handle reply */ }
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onReplyClick: (String) -> Unit = {},
    onShowRepliesClick: (String) -> Unit = {},
    onHideRepliesClick: (String) -> Unit = {},
    showReplies: Boolean = false,
    replies: List<Comment> = emptyList(),
    isLoadingReplies: Boolean = false
) {
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(Spacing.md)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // User info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // User avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comment.userId.take(1).uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Column {
                        Text(
                            text = comment.userId,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = comment.createdAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            // Comment content
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
            
            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Reply button
                    TextButton(
                        onClick = { 
                            onReplyClick(comment.id)
                        }
                    ) {
                        Text(
                            text = "Reply",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Reply count and show/hide replies button
                if (comment.replyCount > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        TextButton(
                            onClick = { 
                                if (showReplies) {
                                    onHideRepliesClick(comment.id)
                                } else {
                                    onShowRepliesClick(comment.id)
                                }
                            }
                        ) {
                            if (isLoadingReplies) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (showReplies) "Hide ${comment.replyCount} replies" else "Show ${comment.replyCount} replies",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            
            // Show replies if expanded
            if (showReplies && comment.replyCount > 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.sm)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                        )
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    if (isLoadingReplies) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else if (replies.isNotEmpty()) {
                        replies.forEach { reply ->
                            ReplyItem(reply = reply)
                        }
                    } else {
                        Text(
                            text = "No replies yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(Spacing.sm)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyItem(
    reply: Comment
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp) // Indent replies
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(Spacing.md)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // User info row with avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // User avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = reply.userId.take(1).uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Text(
                                text = reply.userId,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (reply.replyToUserName != null) {
                                Text(
                                    text = "→ @${reply.replyToUserName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Text(
                            text = reply.createdAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            // Reply content
            Text(
                text = reply.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}