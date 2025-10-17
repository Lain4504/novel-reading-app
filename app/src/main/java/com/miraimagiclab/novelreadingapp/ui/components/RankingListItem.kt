package com.miraimagiclab.novelreadingapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.ui.theme.RankingAccent
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing

@Composable
fun RankingListItem(
    novel: Novel,
    rank: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        // Ranking number with enhanced styling
        Text(
            text = String.format("%02d", rank),
            style = MaterialTheme.typography.headlineMedium,
            color = RankingAccent,
            modifier = Modifier.width(40.dp)
        )
        
        Spacer(modifier = Modifier.width(Spacing.sm))
        
        // Novel thumbnail
        AsyncImage(
            model = novel.coverImage,
            contentDescription = novel.title,
            modifier = Modifier
                .width(55.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(Spacing.sm))
        
        // Novel info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = novel.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(Spacing.xs))
            
            Text(
                text = novel.categories.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(Spacing.xs))
            
            // Rating with improved styling
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = String.format("%.1f", novel.rating),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
