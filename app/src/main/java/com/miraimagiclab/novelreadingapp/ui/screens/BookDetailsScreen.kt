package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.data.MockData
import com.miraimagiclab.novelreadingapp.ui.components.BookCard
import com.miraimagiclab.novelreadingapp.ui.theme.CustomShapes
import com.miraimagiclab.novelreadingapp.ui.theme.ReadButtonColor
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing
import com.miraimagiclab.novelreadingapp.ui.viewmodel.NovelDetailViewModel
import com.miraimagiclab.novelreadingapp.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    bookId: String,
    onBackClick: () -> Unit,
    onChapterClick: (chapter: com.miraimagiclab.novelreadingapp.data.Chapter) -> Unit = {},
    viewModel: NovelDetailViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    // Load novel detail when screen is first displayed
    LaunchedEffect(bookId) {
        viewModel.loadNovelDetail(bookId)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    when (val currentState = uiState) {
        is UiState.Idle -> {
            // Show loading state for idle as well
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refreshData() }) {
                        Text("Retry")
                    }
                }
            }
        }
        is UiState.Success -> {
            val bookDetail = currentState.data
            BookDetailsContent(
                bookDetail = bookDetail,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                onBackClick = onBackClick,
                onChapterClick = onChapterClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookDetailsContent(
    bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    onChapterClick: (chapter: com.miraimagiclab.novelreadingapp.data.Chapter) -> Unit
) {
    val tabs = listOf("Overview", "Chapters", "Comments", "Reviews", "Recom")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = bookDetail.book.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                actions = {
                    IconButton(onClick = { /* Handle favorite */ }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Add to favorites",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = WindowInsets(0)
            )
        },
        bottomBar = {
            Button(
                onClick = { 
                    val firstChapter = bookDetail.chapters.firstOrNull()
                    if (firstChapter != null) {
                        onChapterClick(firstChapter)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReadButtonColor
                ),
                shape = CustomShapes.buttonShape
            ) {
                Text(
                    text = "Start to read",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
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
            
            // Tab Row with horizontal scrolling
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { onTabSelected(index) },
                        text = { 
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
            
            // Tab Content
            when (selectedTabIndex) {
                0 -> OverviewContent(bookDetail = bookDetail)
                1 -> ChaptersContent(bookDetail = bookDetail, onChapterClick = onChapterClick)
                2 -> CommentsContent(bookDetail = bookDetail)
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
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Book Cover
        AsyncImage(
            model = bookDetail.book.coverUrl,
            contentDescription = bookDetail.book.title,
            modifier = Modifier
                .width(120.dp)
                .height(160.dp)
                .clip(CustomShapes.bookCoverShape),
            contentScale = ContentScale.Crop
        )
        
        // Book Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = "Author: ${bookDetail.book.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (bookDetail.book.series != null) {
                Text(
                    text = "Series: ${bookDetail.book.series}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = "Type: ${when (bookDetail.book.type) {
                    com.miraimagiclab.novelreadingapp.data.BookType.NOVEL -> "Novel"
                    com.miraimagiclab.novelreadingapp.data.BookType.LIGHT_NOVEL -> "Light Novel"
                    com.miraimagiclab.novelreadingapp.data.BookType.MANGA -> "Manga"
                }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Genre: ${bookDetail.book.genres.joinToString(" | ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Release: ${bookDetail.book.releaseDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Pages: ${bookDetail.book.readTime}",
                style = MaterialTheme.typography.bodyMedium,
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
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Summary Section
        Text(
            text = "Summary",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = bookDetail.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Illustration Section
        Text(
            text = "Illustration",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=120&h=200&fit=crop",
                contentDescription = "Illustration 1",
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            AsyncImage(
                model = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=120&h=200&fit=crop",
                contentDescription = "Illustration 2",
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ChaptersContent(
    bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail,
    onChapterClick: (chapter: com.miraimagiclab.novelreadingapp.data.Chapter) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        items(bookDetail.chapters) { chapter ->
            ChapterItem(
                chapter = chapter,
                onClick = { onChapterClick(chapter) }
            )
        }
    }
}

@Composable
fun ChapterItem(
    chapter: com.miraimagiclab.novelreadingapp.data.Chapter,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = chapter.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = "Created: ${chapter.createdAt}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun CommentsContent(bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        items(bookDetail.comments) { comment ->
            CommentItem(comment = comment)
        }
    }
}

@Composable
fun CommentItem(comment: com.miraimagiclab.novelreadingapp.data.Comment) {
    var showReplies by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = comment.username,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Text(
            text = comment.comment,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = comment.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Reply button
            if (comment.replies.isNotEmpty()) {
                TextButton(
                    onClick = { showReplies = !showReplies }
                ) {
                    Text(
                        text = if (showReplies) "Hide replies" else "Show ${comment.replies.size} replies",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Show replies if expanded
        if (showReplies && comment.replies.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(start = Spacing.md, top = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                comment.replies.forEach { reply ->
                    ReplyItem(reply = reply)
                }
            }
        }
    }
}

@Composable
fun ReplyItem(reply: com.miraimagiclab.novelreadingapp.data.Reply) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Reply indicator line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(1.dp)
                )
        )
        
        Spacer(modifier = Modifier.width(Spacing.sm))
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = reply.username,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = reply.comment,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
            
            Text(
                text = reply.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ReviewsContent(bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Average Rating Section
        item {
            AverageRatingSection(bookDetail = bookDetail)
        }
        
        // Top Reviews Header
        item {
            TopReviewsHeader()
        }
        
        // Individual Reviews
        items(bookDetail.reviews) { review ->
            DetailedReviewItem(review = review)
        }
    }
}

@Composable
fun AverageRatingSection(bookDetail: com.miraimagiclab.novelreadingapp.data.BookDetail) {
    val averageRating = bookDetail.reviews.map { it.rating }.average()
    val totalReviews = bookDetail.reviews.size
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side - Average rating
        Column {
            Text(
                text = "Average rating",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = "${String.format("%.1f", averageRating)}/5",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Star rating
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Text(
                text = "($totalReviews Reviews)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        // Right side - Rating breakdown
        Column(
            modifier = Modifier.width(120.dp)
        ) {
            // Calculate rating distribution
            val ratingDistribution = (1..5).map { star ->
                bookDetail.reviews.count { it.rating == star }.toFloat() / totalReviews
            }
            
            (5 downTo 1).forEachIndexed { index, star ->
                val percentage = (ratingDistribution[star - 1] * 100).toInt()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$star",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(percentage / 100f)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "${percentage}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(24.dp)
                    )
                }
                
                if (index < 4) {
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
fun TopReviewsHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Top Reviews",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Sort dropdown
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sort by",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Sort",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DetailedReviewItem(review: com.miraimagiclab.novelreadingapp.data.Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // User info row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.username.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(Spacing.sm))
                
                Column {
                    Text(
                        text = review.username,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = review.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Star rating
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
            
            // Review title (if available)
            Text(
                text = "Great read!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Review content
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun ReviewItem(review: com.miraimagiclab.novelreadingapp.data.Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = review.username,
                style = MaterialTheme.typography.titleSmall,
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
        
        Text(
            text = review.date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
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
                book = book,
                onClick = { /* Handle recommendation click */ }
            )
        }
    }
}
