package com.miraimagiclab.novelreadingapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing

@Composable
fun HomeScreenSkeleton(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.contentPadding)
    ) {
        // Banner skeleton
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            brush = brush
        )
        
        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
        
        // Section title skeleton
        SkeletonBox(
            modifier = Modifier
                .width(200.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp)),
            brush = brush
        )
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        // Horizontal list skeleton
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(horizontal = Spacing.xs)
        ) {
            items(5) {
                SkeletonBox(
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    brush = brush
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
        
        // Section title skeleton
        SkeletonBox(
            modifier = Modifier
                .width(150.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp)),
            brush = brush
        )
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        // Horizontal list skeleton
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(horizontal = Spacing.xs)
        ) {
            items(5) {
                SkeletonBox(
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    brush = brush
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
        
        // Section title skeleton
        SkeletonBox(
            modifier = Modifier
                .width(180.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp)),
            brush = brush
        )
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        // Ranking list skeleton
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            repeat(5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    SkeletonBox(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        brush = brush
                    )
                    
                    SkeletonBox(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        brush = brush
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        SkeletonBox(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            brush = brush
                        )
                        SkeletonBox(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            brush = brush
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkeletonBox(
    modifier: Modifier = Modifier,
    brush: Brush
) {
    Box(
        modifier = modifier.background(brush = brush)
    )
}
