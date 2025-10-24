package com.miraimagiclab.novelreadingapp.ui.components.novel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing

data class ReviewRequest(
    val overallRating: Double,
    val writingQuality: Int,
    val stabilityOfUpdates: Int,
    val storyDevelopment: Int,
    val characterDesign: Int,
    val worldBackground: Int,
    val reviewText: String
)

@Composable
fun AddReviewDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (review: ReviewRequest) -> Unit
) {
    if (isVisible) {
        var overallRating by remember { mutableStateOf(3.0) }
        var writingQuality by remember { mutableStateOf(3) }
        var stabilityOfUpdates by remember { mutableStateOf(3) }
        var storyDevelopment by remember { mutableStateOf(3) }
        var characterDesign by remember { mutableStateOf(3) }
        var worldBackground by remember { mutableStateOf(3) }
        var reviewText by remember { mutableStateOf(TextFieldValue("")) }
        
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Add Review")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Rate this novel and share your detailed thoughts:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    // Overall Rating
                    RatingSection(
                        title = "Overall Rating",
                        rating = overallRating,
                        onRatingChange = { overallRating = it }
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
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
                    
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    // Review Text
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        placeholder = { Text("Write your detailed review here...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (reviewText.text.isNotBlank()) {
                            onSubmit(
                                ReviewRequest(
                                    overallRating = overallRating,
                                    writingQuality = writingQuality,
                                    stabilityOfUpdates = stabilityOfUpdates,
                                    storyDevelopment = storyDevelopment,
                                    characterDesign = characterDesign,
                                    worldBackground = worldBackground,
                                    reviewText = reviewText.text
                                )
                            )
                            onDismiss()
                        }
                    },
                    enabled = reviewText.text.isNotBlank()
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
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
            style = MaterialTheme.typography.titleSmall,
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
