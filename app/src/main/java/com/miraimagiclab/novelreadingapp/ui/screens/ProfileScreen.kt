package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.data.MockData
import com.miraimagiclab.novelreadingapp.ui.components.StatsCard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star

@Composable
fun ProfileScreen() {
    val stats = MockData.userStats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200&h=200",
            contentDescription = "Profile avatar",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Reader", style = MaterialTheme.typography.titleMedium)
        Text("Book lover & story seeker", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(24.dp))

        StatsCard(
            icon = Icons.Default.Favorite,
            title = "Books Read",
            value = "${
                (MockData.recommendedBooks + MockData.ourPickBooks).count { it.isCompleted }
            }"
        )
        Spacer(modifier = Modifier.height(12.dp))
        StatsCard(
            icon = Icons.Default.Star,
            title = "Points",
            value = "${stats.bookPoints}"
        )
    }
}
