package com.miraimagiclab.novelreadingapp.ui.components.novel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.domain.model.NovelDetail
import com.miraimagiclab.novelreadingapp.ui.theme.CustomShapes
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing

@Composable
fun NovelHeader(novelDetail: NovelDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Book Cover - Made larger
        AsyncImage(
            model = novelDetail.novel.coverImage,
            contentDescription = novelDetail.novel.title,
            modifier = Modifier
                .width(140.dp)
                .height(200.dp)
                .clip(CustomShapes.bookCoverShape),
            contentScale = ContentScale.Crop
        )
        
        // Book Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Title - Most prominent
            Text(
                text = novelDetail.novel.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Author
            Text(
                text = "by ${novelDetail.novel.authorName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            
            // Status
            Text(
                text = "Status: ${novelDetail.novel.status.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            // Categories
            if (novelDetail.novel.categories.isNotEmpty()) {
                Text(
                    text = "Categories: ${novelDetail.novel.categories.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Word count
            Text(
                text = "Word count: ${novelDetail.novel.wordCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Chapter count
            Text(
                text = "Chapters: ${novelDetail.novel.chapterCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // View count
            Text(
                text = "Views: ${novelDetail.novel.viewCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Follow count
            Text(
                text = "Follows: ${novelDetail.novel.followCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Rating
            Text(
                text = "Rating: ${String.format("%.1f", novelDetail.novel.rating)} (${novelDetail.novel.ratingCount} reviews)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Created date
            Text(
                text = "Created: ${novelDetail.novel.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}