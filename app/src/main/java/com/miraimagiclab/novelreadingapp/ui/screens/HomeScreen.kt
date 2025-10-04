package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.ui.components.BannerCard
import com.miraimagiclab.novelreadingapp.ui.components.BookCard
import com.miraimagiclab.novelreadingapp.ui.viewmodel.HomeViewModel
import com.miraimagiclab.novelreadingapp.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    
    // Collect states from ViewModel
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val featuredBook by viewModel.featuredBook.collectAsStateWithLifecycle()
    val recommendedBooks by viewModel.recommendedBooks.collectAsStateWithLifecycle()
    val ourPicks by viewModel.ourPicks.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Welcome back,",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        when (val stats = userStats) {
                            is UserStatsState.Success -> {
                                stats.data.userName?.let {
                                    Text(
                                        text = it,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            is UserStatsState.Loading -> {
                                Text(
                                    text = "Loading...",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            is UserStatsState.Error -> {
                                Text(
                                    text = "Guest User",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notification */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                    Text(
                        text = "12:30",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 16.dp),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle profile */ }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // User Stats Section
            UserStatsSection(userStats = userStats)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Banner Section
            when (val featured = featuredBook) {
                is FeaturedBookState.Success -> {
                    BannerCard(
                        title = featured.data.title,
                        subtitle = featured.data.series,
                        imageUrl = featured.data.coverUrl
                    )
                }
                is FeaturedBookState.Loading -> {
                    BannerLoadingCard()
                }
                is FeaturedBookState.Error -> {
                    BannerErrorCard(
                        onRetry = { viewModel.refreshData() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Recommended for you section
            Text(
                text = "Recommended for you",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            when (val books = recommendedBooks) {
                is BooksState.Success -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(books.data) { book ->
                            BookCard(
                                book = book,
                                onClick = { book.id?.let { onBookClick(it) } }
                            )
                        }
                    }
                }
                is BooksState.Loading -> {
                    BooksLoadingRow()
                }
                is BooksState.Error -> {
                    BooksErrorRow(
                        message = books.message,
                        onRetry = { viewModel.refreshData() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Our pick for novel section
            Text(
                text = "Our pick for novel",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            when (val picks = ourPicks) {
                is PickBooksState.Success -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(picks.data) { book ->
                            PickBookCard(
                                book = book,
                                onClick = { book.id?.let { onBookClick(it) } }
                            )
                        }
                    }
                }
                is PickBooksState.Loading -> {
                    BooksLoadingRow()
                }
                is PickBooksState.Error -> {
                    BooksErrorRow(
                        message = picks.message,
                        onRetry = { viewModel.refreshData() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UserStatsSection(userStats: UserStatsState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        when (val stats = userStats) {
            is UserStatsState.Success -> {
                UserStatItem(
                    icon = Icons.Default.Star,
                    label = "Book Points",
                    value = (stats.data.bookPoints ?: 0).toString()
                )
                UserStatItem(
                    icon = Icons.Default.AccountCircle,
                    label = "Read Books",
                    value = (stats.data.readBooks ?: 0).toString()
                )
            }
            is UserStatsState.Loading -> {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
            is UserStatsState.Error -> {
                Text(
                    text = "Failed to load user stats",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun UserStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun BannerLoadingCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun BannerErrorCard(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Failed to load banner",
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun BooksLoadingRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(3) {
            Box(
                modifier = Modifier
                    .size(120.dp, 180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

@Composable
private fun BooksErrorRow(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Failed to load books: $message",
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun PickBookCard(
    book: com.miraimagiclab.novelreadingapp.data.model.PickBook,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp, 180.dp)
            .clip(RoundedCornerShape(8.dp)),
        onClick = onClick
    ) {
        Column {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            book.title?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            book.author?.let {
                Text(
                    text = it,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}
