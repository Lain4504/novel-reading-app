package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.ui.components.novel.ReviewRequest
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing
import com.miraimagiclab.novelreadingapp.ui.viewmodel.CreateReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReviewScreen(
    novelId: String,
    onBackClick: () -> Unit,
    onReviewSubmitted: () -> Unit,
    viewModel: CreateReviewViewModel = hiltViewModel()
) {
    var overallRating by remember { mutableStateOf(3.0) }
    var writingQuality by remember { mutableStateOf(3) }
    var stabilityOfUpdates by remember { mutableStateOf(3) }
    var storyDevelopment by remember { mutableStateOf(3) }
    var characterDesign by remember { mutableStateOf(3) }
    var worldBackground by remember { mutableStateOf(3) }
    var reviewText by remember { mutableStateOf(TextFieldValue("")) }
    
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val submitResult by viewModel.submitResult.collectAsState()
    val novelInfo by viewModel.novelInfo.collectAsState()
    
    // Load novel info when screen opens
    LaunchedEffect(novelId) {
        viewModel.loadNovelInfo(novelId)
    }
    
    // Handle submit result
    LaunchedEffect(submitResult) {
        when (submitResult) {
            is com.miraimagiclab.novelreadingapp.util.UiState.Success -> {
                onReviewSubmitted()
            }
            is com.miraimagiclab.novelreadingapp.util.UiState.Error -> {
                // Error will be shown in the UI below
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Write Review",
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            if (reviewText.text.isNotBlank()) {
                                val review = ReviewRequest(
                                    overallRating = overallRating,
                                    writingQuality = writingQuality,
                                    stabilityOfUpdates = stabilityOfUpdates,
                                    storyDevelopment = storyDevelopment,
                                    characterDesign = characterDesign,
                                    worldBackground = worldBackground,
                                    reviewText = reviewText.text
                                )
                                viewModel.submitReview(novelId, review)
                            }
                        },
                        enabled = reviewText.text.isNotBlank() && !isSubmitting,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Submit Review")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Text(
                text = "Rate this novel and share your detailed thoughts:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Error Message Display
            when (val result = submitResult) {
                is com.miraimagiclab.novelreadingapp.util.UiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                Text(
                                    text = "Failed to submit review",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = result.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                // Show specific error handling for common issues
                                when {
                                    result.message.contains("403") || result.message.contains("Forbidden") -> {
                                        Text(
                                            text = "You may not have permission to review this novel, or your session has expired. Please try logging in again.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                    result.message.contains("401") || result.message.contains("Unauthorized") -> {
                                        Text(
                                            text = "Please log in to submit a review.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                    result.message.contains("User not logged in") -> {
                                        Text(
                                            text = "Please log in to submit a review.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                }
                                
                                // Retry button for errors
                                TextButton(
                                    onClick = {
                                        // Clear the error state to allow retry
                                        viewModel.clearSubmitResult()
                                    },
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Text(
                                        text = "Try Again",
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
            
            // Reading Progress Info
            when (val currentNovelInfo = novelInfo) {
                is com.miraimagiclab.novelreadingapp.util.UiState.Success -> {
                    val (chaptersRead, totalChapters) = currentNovelInfo.data
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.md),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            Text(
                                text = "Your Reading Progress",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Chapters read: $chaptersRead / $totalChapters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            if (chaptersRead > 0) {
                                val progressPercentage = (chaptersRead.toFloat() / totalChapters * 100).toInt()
                                Text(
                                    text = "Progress: $progressPercentage%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                is com.miraimagiclab.novelreadingapp.util.UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
                is com.miraimagiclab.novelreadingapp.util.UiState.Error -> {
                    Text(
                        text = "Could not load reading progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
            
            // Overall Rating
            RatingSection(
                title = "Overall Rating",
                rating = overallRating,
                onRatingChange = { overallRating = it }
            )
            
            // Detailed Ratings
            RatingSection(
                title = "Writing Quality",
                rating = writingQuality.toDouble(),
                onRatingChange = { writingQuality = it.toInt() }
            )
            
            RatingSection(
                title = "Stability of Updates",
                rating = stabilityOfUpdates.toDouble(),
                onRatingChange = { stabilityOfUpdates = it.toInt() }
            )
            
            RatingSection(
                title = "Story Development",
                rating = storyDevelopment.toDouble(),
                onRatingChange = { storyDevelopment = it.toInt() }
            )
            
            RatingSection(
                title = "Character Design",
                rating = characterDesign.toDouble(),
                onRatingChange = { characterDesign = it.toInt() }
            )
            
            RatingSection(
                title = "World Background",
                rating = worldBackground.toDouble(),
                onRatingChange = { worldBackground = it.toInt() }
            )
            
            // Review Text
            Text(
                text = "Review Text",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                placeholder = { Text("Write your detailed review here...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8
            )
        }
    }
}

@Composable
private fun RatingSection(
    title: String,
    rating: Double,
    onRatingChange: (Double) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Slider(
            value = rating.toFloat(),
            onValueChange = { onRatingChange(it.toDouble()) },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "1",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "5",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
