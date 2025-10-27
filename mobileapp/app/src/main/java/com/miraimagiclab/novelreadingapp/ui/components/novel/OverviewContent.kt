package com.miraimagiclab.novelreadingapp.ui.components.novel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.domain.model.NovelDetail
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing

@Composable
fun OverviewContent(novelDetail: NovelDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl)
    ) {
        // Summary Section
        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
        
        // Summary content with subtle background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(Spacing.md)
        ) {
            Text(
                text = novelDetail.novel.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )
        }
        
        // Illustration Section
        Text(
            text = "Illustration",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=120&h=200&fit=crop",
                contentDescription = "Illustration 1",
                modifier = Modifier
                    .width(70.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
            AsyncImage(
                model = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=120&h=200&fit=crop",
                contentDescription = "Illustration 2",
                modifier = Modifier
                    .width(70.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}