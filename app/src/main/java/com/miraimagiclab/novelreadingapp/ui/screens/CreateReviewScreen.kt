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
                )
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
                                onReviewSubmitted()
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
