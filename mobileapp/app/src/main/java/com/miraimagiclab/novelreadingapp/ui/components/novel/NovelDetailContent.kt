package com.miraimagiclab.novelreadingapp.ui.components.novel

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.miraimagiclab.novelreadingapp.domain.model.*
import com.miraimagiclab.novelreadingapp.ui.theme.CustomShapes
import com.miraimagiclab.novelreadingapp.ui.theme.ReadButtonColor
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovelDetailContent(
    novelDetail: NovelDetail,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    onChapterClick: (chapter: Chapter) -> Unit,
    onNovelClick: (novelId: String) -> Unit = {},
    onNavigateToComments: (novelId: String) -> Unit = {},
    onNavigateToCreateReview: (novelId: String) -> Unit = {},
    viewModel: com.miraimagiclab.novelreadingapp.ui.viewmodel.NovelDetailViewModel
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
                    val userInteraction by viewModel.userInteraction.collectAsState()
                    IconButton(onClick = { viewModel.toggleFollow() }) {
                        Icon(
                            imageVector = if (userInteraction?.hasFollowing == true) Icons.Default.Star else Icons.Default.Star,
                            contentDescription = if (userInteraction?.hasFollowing == true) "Remove from favorites" else "Add to favorites",
                            tint = if (userInteraction?.hasFollowing == true) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary
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
            val lastReadChapter = viewModel.getLastReadChapter()
            val buttonText = if (lastReadChapter != null) "Continue Reading" else "Start to read"
            val targetChapter = lastReadChapter ?: novelDetail.chapters.firstOrNull()
            
            Button(
                onClick = { 
                    if (targetChapter != null) {
                        onChapterClick(targetChapter)
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
                    text = buttonText,
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
            NovelHeader(novelDetail = novelDetail)
            
            // Tab Row with horizontal scrolling
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { 
                            if (index == 2) { // Comments tab
                                onNavigateToComments(novelDetail.novel.id)
                                // Don't update selectedTabIndex for Comments as we navigate away
                            } else {
                                onTabSelected(index)
                            }
                        },
                        text = { 
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Medium else FontWeight.Normal,
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
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
                2 -> {
                    // Comments tab - this should not be reached as navigation happens in onClick
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Empty content as navigation should happen immediately
                    }
                }
                3 -> ReviewsContent(
                    novelDetail = novelDetail,
                    onNavigateToCreateReview = onNavigateToCreateReview
                )
                4 -> RecommendationsContent(
                    novelDetail = novelDetail,
                    onNovelClick = onNovelClick
                )
            }
        }
    }
}