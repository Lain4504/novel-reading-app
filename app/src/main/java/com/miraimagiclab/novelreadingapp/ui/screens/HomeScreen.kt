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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.ui.components.BannerCard
import com.miraimagiclab.novelreadingapp.ui.components.NovelCard
import com.miraimagiclab.novelreadingapp.ui.components.RankingListItem
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing
import com.miraimagiclab.novelreadingapp.ui.viewmodel.HomeViewModel
import com.miraimagiclab.novelreadingapp.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNovelClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    
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
        when (val currentState = uiState) {
            is UiState.Idle -> {
                // Show loading state for idle as well
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
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
                val homeData = currentState.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(horizontal = Spacing.contentPadding)
                ) {
                    // Banner Section with swipeable ranking novels
                    if (homeData.bannerNovels.isNotEmpty()) {
                        val bannerPagerState = rememberPagerState(pageCount = { homeData.bannerNovels.size })
                        
                        HorizontalPager(
                            state = bannerPagerState,
                            modifier = Modifier.height(200.dp)
                        ) { page ->
                            val novel = homeData.bannerNovels[page]
                            BannerCard(
                                title = novel.title,
                                subtitle = "Rank #${page + 1} • ${novel.authorName}",
                                imageUrl = novel.coverImage ?: "",
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
                    }
                    
                    // Recommended for you section
                    if (homeData.recommendedNovels.isNotEmpty()) {
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
                            items(homeData.recommendedNovels) { novel ->
                                NovelCard(
                                    novel = novel,
                                    onClick = { onNovelClick(novel.id) }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
                    }
                    
                    // Our pick for novel section
                    if (homeData.newNovels.isNotEmpty()) {
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
                            items(homeData.newNovels.take(5)) { novel ->
                                NovelCard(
                                    novel = novel,
                                    onClick = { onNovelClick(novel.id) }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
                    }
                    
                    // Ranking section
                    if (homeData.rankingNovels.isNotEmpty()) {
                        Text(
                            text = "Top Ranking",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = Spacing.md)
                        )
                        
                        // Ranking with swiper functionality
                        val pagerState = rememberPagerState(pageCount = { (homeData.rankingNovels.size + 4) / 5 })
                        
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
                                    if (globalIndex < homeData.rankingNovels.size) {
                                        val novel = homeData.rankingNovels[globalIndex]
                                        RankingListItem(
                                            novel = novel,
                                            rank = globalIndex + 1,
                                            onClick = { onNovelClick(novel.id) }
                                        )
                                    } else {
                                        // Empty space to maintain consistent layout
                                        Spacer(modifier = Modifier.height(88.dp))
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
                    }
                    
                    // New Novels section
                    if (homeData.newNovels.isNotEmpty()) {
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
                            items(homeData.newNovels) { novel ->
                                NovelCard(
                                    novel = novel,
                                    onClick = { onNovelClick(novel.id) }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
                    }
                    
                    // Completed Novels section
                    if (homeData.completedNovels.isNotEmpty()) {
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
                            items(homeData.completedNovels) { novel ->
                                NovelCard(
                                    novel = novel,
                                    onClick = { onNovelClick(novel.id) }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
                    }
                }
            }
        }
    }
}
