package com.miraimagiclab.novelreadingapp.ui.components.novel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.domain.model.*
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing

@Composable
fun RecommendationsContent(
    novelDetail: NovelDetail,
    onNovelClick: (novelId: String) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Recommendations Header with count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recommendations (${novelDetail.recommendations.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Refresh recommendations button (placeholder for future enhancement)
            IconButton(onClick = { /* Handle refresh recommendations */ }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh recommendations",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (novelDetail.recommendations.isEmpty()) {
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
                        contentDescription = "No recommendations",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No recommendations available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Check back later for similar novels!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(novelDetail.recommendations) { novel ->
                    RecommendationCard(
                        novel = novel,
                        onClick = { onNovelClick(novel.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(
    novel: Novel,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(Spacing.sm)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            // Book cover
            AsyncImage(
                model = novel.coverImage,
                contentDescription = novel.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Book title
            Text(
                text = novel.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Author
            Text(
                text = novel.authorName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = String.format("%.1f", novel.rating),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}