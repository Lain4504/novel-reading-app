package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraimagiclab.novelreadingapp.data.MockData
import com.miraimagiclab.novelreadingapp.ui.components.BannerCard
import com.miraimagiclab.novelreadingapp.ui.components.BookCard
import com.miraimagiclab.novelreadingapp.ui.components.RankingListItem
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Welcome back,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Cheyenne Curtis",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notification */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle profile */ }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = Spacing.contentPadding)
        ) {
            // Banner Section with swipeable ranking books
            val bannerPagerState = rememberPagerState(pageCount = { MockData.rankingBooks.size })
            
            HorizontalPager(
                state = bannerPagerState,
                modifier = Modifier.height(200.dp)
            ) { page ->
                val book = MockData.rankingBooks[page]
                BannerCard(
                    title = book.title,
                    subtitle = "Rank #${page + 1} • ${book.author}",
                    imageUrl = book.coverUrl
                )
            }
            
            // Banner page indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(bannerPagerState.pageCount) { iteration ->
                    val color = if (bannerPagerState.currentPage == iteration) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.outline
                    
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(6.dp)
                            .background(
                                color = color,
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
            
            // Recommended for you section
            Text(
                text = "Recommended for you",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(horizontal = Spacing.xs)
            ) {
                items(MockData.recommendedBooks) { book ->
                    BookCard(
                        book = book,
                        onClick = { onBookClick(book.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
            
            // Our pick for novel section
            Text(
                text = "Our pick for novel",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(horizontal = Spacing.xs)
            ) {
                items(MockData.ourPickBooks) { book ->
                    BookCard(
                        book = book,
                        onClick = { onBookClick(book.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
            
            // Ranking section
            Text(
                text = "Top Ranking",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
            
            // Ranking with swiper functionality
            val pagerState = rememberPagerState(pageCount = { (MockData.rankingBooks.size + 4) / 5 })
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.height(480.dp)
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Show 5 items per page
                    repeat(5) { index ->
                        val globalIndex = page * 5 + index
                        if (globalIndex < MockData.rankingBooks.size) {
                            RankingListItem(
                                book = MockData.rankingBooks[globalIndex],
                                rank = globalIndex + 1,
                                onClick = { onBookClick(MockData.rankingBooks[globalIndex].id) }
                            )
                        } else {
                            // Empty space to maintain consistent layout
                            Spacer(modifier = Modifier.height(88.dp))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
            
            // New Books section
            Text(
                text = "Truyện mới",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(horizontal = Spacing.xs)
            ) {
                items(MockData.newBooks) { book ->
                    BookCard(
                        book = book,
                        onClick = { onBookClick(book.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
            
            // Completed Books section
            Text(
                text = "Truyện hoàn thành",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(horizontal = Spacing.xs)
            ) {
                items(MockData.completedBooks) { book ->
                    BookCard(
                        book = book,
                        onClick = { onBookClick(book.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
        }
    }
}
