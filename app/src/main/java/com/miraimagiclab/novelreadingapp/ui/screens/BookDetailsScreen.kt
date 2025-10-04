package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.data.MockData
import com.miraimagiclab.novelreadingapp.ui.components.BookCard
import com.miraimagiclab.novelreadingapp.ui.theme.ReadButtonColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    bookId: String,
    onBackClick: () -> Unit
) {
    val bookDetail = MockData.getBookDetail(bookId)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    if (bookDetail == null) {
        // Handle book not found
        return
    }
    
    val tabs = listOf("Overview", "Chapters", "Series volume", "Reviews", "Recom")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = bookDetail.book.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle favorite */ }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Add to favorites"
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
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { /* Handle start reading */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReadButtonColor
                )
            ) {
                Text(
                    text = "Start to read",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Book Header
            BookHeader(bookDetail = bookDetail)
            
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Tab Content
            when (selectedTabIndex) {
                0 -> OverviewContent(bookDetail = bookDetail)
                1 -> ChaptersContent(bookDetail = bookDetail)
                2 -> SeriesVolumeContent(bookDetail = bookDetail)
                3 -> ReviewsContent(bookDetail = bookDetail)
                4 -> RecommendationsContent(bookDetail = bookDetail)
            }
        }
    }
}

@Composable
fun BookHeader(bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Book Cover
        AsyncImage(
            model = bookDetail.book.coverUrl,
            contentDescription = bookDetail.book.title,
            modifier = Modifier
                .width(120.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        // Book Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Author: ${bookDetail.book.author}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (bookDetail.book.series != null) {
                Text(
                    text = "Series: ${bookDetail.book.series}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = "Type: ${when (bookDetail.book.type) {
                    com.miraimagiclab.novelreadingapp.data.BookType.NOVEL -> "Novel"
                    com.miraimagiclab.novelreadingapp.data.BookType.LIGHT_NOVEL -> "Light Novel"
                    com.miraimagiclab.novelreadingapp.data.BookType.MANGA -> "Manga"
                }}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Genre: ${bookDetail.book.genres.joinToString(" | ")}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Release: ${bookDetail.book.releaseDate}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Pages: ${bookDetail.book.readTime}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun OverviewContent(bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Section
        Text(
            text = "Summary",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = bookDetail.summary,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
        
        // Illustration Section
        Text(
            text = "Illustration",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=150&h=150&fit=crop",
                contentDescription = "Illustration 1",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            AsyncImage(
                model = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=150&h=150&fit=crop",
                contentDescription = "Illustration 2",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ChaptersContent(bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(bookDetail.chapters) { chapter ->
            ChapterItem(chapter = chapter)
        }
    }
}

@Composable
fun ChapterItem(chapter: com.miraimagiclab.novelreadingapp.data.Chapter) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = chapter.thumbnailUrl ?: "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=60&h=60&fit=crop",
            contentDescription = chapter.title,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = chapter.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SeriesVolumeContent(bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(bookDetail.volumes) { volume ->
            VolumeItem(volume = volume)
        }
    }
}

@Composable
fun VolumeItem(volume: com.miraimagiclab.novelreadingapp.data.Volume) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = volume.coverUrl,
            contentDescription = volume.title,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = volume.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = volume.releaseDate,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ReviewsContent(bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(bookDetail.reviews) { review ->
            ReviewItem(review = review)
        }
    }
}

@Composable
fun ReviewItem(review: com.miraimagiclab.novelreadingapp.data.Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.username,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
            
            Text(
                text = review.date,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun RecommendationsContent(bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(bookDetail.recommendations) { book ->
            BookCard(
                book = com.miraimagiclab.novelreadingapp.data.model.Book(
                    id = book.id,
                    title = book.title,
                    type = book.type.name,
                    score = book.score,
                    coverUrl = book.coverUrl,
                    genres = book.genres
                ),
                onClick = { /* Handle recommendation click */ }
            )
        }
    }
}
