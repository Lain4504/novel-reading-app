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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.ui.components.BannerCard
import com.miraimagiclab.novelreadingapp.ui.components.ErrorState
import com.miraimagiclab.novelreadingapp.ui.components.HomeScreenSkeleton
import com.miraimagiclab.novelreadingapp.ui.components.NovelCard
import com.miraimagiclab.novelreadingapp.ui.components.RankingListItem
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing
import com.miraimagiclab.novelreadingapp.ui.viewmodel.HomeViewModel
import com.miraimagiclab.novelreadingapp.util.HapticFeedback
import com.miraimagiclab.novelreadingapp.util.UiState
import com.miraimagiclab.novelreadingapp.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNovelClick: (String) -> Unit,
    onLoginClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val hapticFeedback = rememberHapticFeedback()
    val authState by viewModel.authState.collectAsState()
    
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    if (authState.isLoggedIn) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Text(
                                text = "Welcome back,",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = authState.username ?: "User",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Text(
                            text = "Novel Reading App",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (authState.isLoggedIn) {
                        IconButton(onClick = { /* Handle notification */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (authState.isLoggedIn) {
                        IconButton(onClick = { /* Handle profile */ }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        IconButton(onClick = onLoginClick) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Login",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
                // Show skeleton loading state
                HomeScreenSkeleton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is UiState.Loading -> {
                // Show skeleton loading state
                HomeScreenSkeleton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is UiState.Error -> {
                ErrorState(
                    message = currentState.message,
                    onRetry = { viewModel.refreshData() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
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
                                onClick = {
                                    hapticFeedback.light()
                                    onNovelClick(novel.id)
                                }
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
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
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
                                    onClick = { 
                                        hapticFeedback.light()
                                        onNovelClick(novel.id) 
                                    }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
                    }
                    
                    // Our pick for novel section
                    if (homeData.newNovels.isNotEmpty()) {
                        Text(
                            text = "Our pick for novel",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
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
                                    onClick = { 
                                        hapticFeedback.light()
                                        onNovelClick(novel.id) 
                                    }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
                    }
                    
                    // Ranking section
                    if (homeData.rankingNovels.isNotEmpty()) {
                        Text(
                            text = "Top Ranking",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
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
                                            onClick = { 
                                                hapticFeedback.light()
                                                onNovelClick(novel.id) 
                                            }
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
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
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
                                    onClick = { 
                                        hapticFeedback.light()
                                        onNovelClick(novel.id) 
                                    }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
                    }
                    
                    // Completed Novels section
                    if (homeData.completedNovels.isNotEmpty()) {
                        Text(
                            text = "Truyện hoàn thành",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
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
                                    onClick = { 
                                        hapticFeedback.light()
                                        onNovelClick(novel.id) 
                                    }
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
