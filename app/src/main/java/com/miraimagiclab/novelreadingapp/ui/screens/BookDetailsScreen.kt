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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.miraimagiclab.novelreadingapp.domain.model.*
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
    onChapterClick: (chapter: Chapter) -> Unit = {},
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
            // Show skeleton loading state
            BookDetailsSkeleton(
                onBackClick = onBackClick
            )
        }
        is UiState.Loading -> {
            // Show skeleton loading state
            BookDetailsSkeleton(
                onBackClick = onBackClick
            )
        }
        is UiState.Error -> {
            BookDetailsErrorState(
                errorMessage = currentState.message,
                onRetry = { viewModel.refreshData() },
                onBackClick = onBackClick
            )
        }
        is UiState.Success -> {
            val novelDetail = currentState.data
            BookDetailsContent(
                novelDetail = novelDetail,
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
    novelDetail: NovelDetail,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    onChapterClick: (chapter: Chapter) -> Unit
) {
    val tabs = listOf("Overview", "Chapters", "Comments", "Reviews", "Recom")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = novelDetail.novel.title,
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
                    val firstChapter = novelDetail.chapters.firstOrNull()
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
            BookHeader(novelDetail = novelDetail)
            
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
                0 -> OverviewContent(novelDetail = novelDetail)
                1 -> ChaptersContent(novelDetail = novelDetail, onChapterClick = onChapterClick)
                2 -> CommentsContent(novelDetail = novelDetail)
                3 -> ReviewsContent(novelDetail = novelDetail)
                4 -> RecommendationsContent(novelDetail = novelDetail)
            }
        }
    }
}

@Composable
fun BookHeader(novelDetail: NovelDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Book Cover
        AsyncImage(
            model = novelDetail.novel.coverImage,
            contentDescription = novelDetail.novel.title,
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
                text = "Author: ${novelDetail.novel.authorName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Status: ${novelDetail.novel.status.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Categories: ${novelDetail.novel.categories.joinToString(" | ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Created: ${novelDetail.novel.createdAt}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Word Count: ${novelDetail.novel.wordCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun OverviewContent(novelDetail: NovelDetail) {
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
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = novelDetail.novel.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp,
                modifier = Modifier.padding(Spacing.md)
            )
        }
        
        // Illustration Section
        Text(
            text = "Illustration",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
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
    novelDetail: NovelDetail,
    onChapterClick: (chapter: Chapter) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Chapters Header with count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chapters (${novelDetail.chapters.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Sort options (placeholder for future enhancement)
            IconButton(onClick = { /* Handle sort */ }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Sort chapters",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (novelDetail.chapters.isEmpty()) {
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
                        contentDescription = "No chapters",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No chapters available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(novelDetail.chapters) { chapter ->
                    ChapterItem(
                        chapter = chapter,
                        onClick = { onChapterClick(chapter) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterItem(
    chapter: Chapter,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chapter.chapterTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "Ch. ${chapter.chapterNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Views",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${chapter.viewCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Word count",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${chapter.wordCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Text(
                    text = chapter.createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun CommentsContent(novelDetail: NovelDetail) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Comments Header with count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comments (${novelDetail.comments.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Add comment button (placeholder for future enhancement)
            IconButton(onClick = { /* Handle add comment */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add comment",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (novelDetail.comments.isEmpty()) {
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
                        contentDescription = "No comments",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No comments yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Be the first to comment!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(novelDetail.comments) { comment ->
                    CommentItem(comment = comment)
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    var showReplies by remember { mutableStateOf(false) }
    
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // User avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comment.userId.take(1).uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Column {
                        Text(
                            text = comment.userId,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = comment.createdAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                // Like button (placeholder)
                IconButton(
                    onClick = { /* Handle like */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Like comment",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Comment content
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
            
            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Like count
                    if (comment.likeCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Likes",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = comment.likeCount.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    // Reply button
                    TextButton(
                        onClick = { showReplies = !showReplies }
                    ) {
                        Text(
                            text = if (showReplies) "Hide replies" else "Reply",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Reply count
                if (comment.replyCount > 0) {
                    TextButton(
                        onClick = { showReplies = !showReplies }
                    ) {
                        Text(
                            text = "${comment.replyCount} replies",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Show replies if expanded
            if (showReplies && comment.replyCount > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.sm),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(
                            text = "Replies functionality requires separate API call to load replies for comment ID: ${comment.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        Text(
                            text = "Use CommentApiService.getRepliesByCommentId() to load replies",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ReviewsContent(novelDetail: NovelDetail) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Reviews Header with count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reviews (${novelDetail.reviews.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Add review button (placeholder for future enhancement)
            IconButton(onClick = { /* Handle add review */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add review",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (novelDetail.reviews.isEmpty()) {
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
                        imageVector = Icons.Default.Star,
                        contentDescription = "No reviews",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No reviews yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Be the first to review this novel!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                // Average Rating Section
                item {
                    AverageRatingSection(novelDetail = novelDetail)
                }
                
                // Top Reviews Header
                item {
                    TopReviewsHeader()
                }
                
                // Individual Reviews
                items(novelDetail.reviews) { review ->
                    DetailedReviewItem(review = review)
                }
            }
        }
    }
}

@Composable
fun AverageRatingSection(novelDetail: NovelDetail) {
    val averageRating = novelDetail.reviews.map { it.overallRating }.average()
    val totalReviews = novelDetail.reviews.size
    
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
                novelDetail.reviews.count { it.overallRating.toInt() == star }.toFloat() / totalReviews
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
fun DetailedReviewItem(review: Review) {
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
                        text = review.userId.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(Spacing.sm))
                
                Column {
                    Text(
                        text = review.userId,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = review.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Star rating
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(review.overallRating.toInt()) {
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
                text = review.reviewText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}


@Composable
fun RecommendationsContent(novelDetail: NovelDetail) {
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
                        onClick = { /* Handle recommendation click */ }
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
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            // Book cover
            AsyncImage(
                model = novel.coverImage,
                contentDescription = novel.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsSkeleton(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(24.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = WindowInsets(0)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(Spacing.lg)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Book Header Skeleton
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                // Book Cover Skeleton
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(160.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                
                // Book Info Skeleton
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    repeat(5) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
            
            // Tab Row Skeleton
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            // Content Skeleton
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                repeat(8) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleLarge
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = WindowInsets(0)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                modifier = Modifier.padding(Spacing.xl)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
                
                Text(
                    text = "Something went wrong",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text("Try Again")
                }
            }
        }
    }
}
