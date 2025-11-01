package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import com.miraimagiclab.novelreadingapp.R
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.model.NovelStatus
import com.miraimagiclab.novelreadingapp.ui.components.ErrorState
import com.miraimagiclab.novelreadingapp.ui.components.NovelCard
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ExploreViewModel
import com.miraimagiclab.novelreadingapp.util.UiState


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExploreScreen(
    onBookClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val exploreState by viewModel.exploreState.collectAsState()
    
    val pullRefreshState = rememberPullRefreshState(
        refreshing = exploreState.isRefreshing,
        onRefresh = { viewModel.refreshData() }
    )
    
    // Show snackbar for errors
    exploreState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // Error will be shown in the UI
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        // Show error state only for initial load errors when there are no novels
        if (exploreState.errorMessage != null && exploreState.novels.isEmpty() && !exploreState.isLoading) {
            ErrorState(
                message = exploreState.errorMessage!!,
                onRetry = { 
                    if (exploreState.searchQuery.isBlank()) {
                        viewModel.refreshData()
                    } else {
                        viewModel.updateSearchQuery(exploreState.searchQuery)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Main content
            ExploreContent(
                state = exploreState,
                onBookClick = onBookClick,
                onBackClick = onBackClick,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onLoadMore = { viewModel.loadMoreResults() },
                onUpdateSort = { sortBy, direction -> viewModel.updateSortOptions(sortBy, direction) }
            )
        }
        
        PullRefreshIndicator(
            refreshing = exploreState.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun ExploreContent(
    state: com.miraimagiclab.novelreadingapp.ui.viewmodel.ExploreState,
    onBookClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLoadMore: () -> Unit,
    onUpdateSort: (String, String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Latest Updated") }
    var showFilterMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top bar with Back + Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Explore Books",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search + Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (state.searchQuery.isEmpty()) {
                                Text(
                                    "Search by title...",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Filter button
            Box {
                IconButton(onClick = { showFilterMenu = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter_alt_24px),
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
                ) {
                    listOf(
                        "Latest Updated" to ("updatedAt" to "desc"),
                        "Oldest Updated" to ("updatedAt" to "asc"),
                        "Latest Created" to ("createdAt" to "desc"),
                        "Oldest Created" to ("createdAt" to "asc"),
                        "Highest Rating" to ("rating" to "desc"),
                        "Lowest Rating" to ("rating" to "asc")
                    ).forEach { (displayName, sortOptions) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    displayName,
                                    fontWeight = if (selectedFilter == displayName)
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedFilter = displayName
                                showFilterMenu = false
                                onUpdateSort(sortOptions.first, sortOptions.second)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show novels or loading/empty state
        when {
            state.isLoading -> {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            state.novels.isNotEmpty() -> {
                // Show novels list with infinite scroll
                LaunchedEffect(listState, state.hasMorePages, state.isLoadingMore) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                        .collect { visibleItems ->
                            if (visibleItems.isNotEmpty() && state.hasMorePages && !state.isLoadingMore) {
                                val lastVisibleItem = visibleItems.last()
                                val totalItems = listState.layoutInfo.totalItemsCount
                                
                                if (lastVisibleItem.index >= totalItems - 3) {
                                    onLoadMore()
                                }
                            }
                        }
                }
                
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(vertical = Spacing.sm)
                ) {
                    val chunkedResults = state.novels.chunked(2)
                    items(chunkedResults.size) { chunkIndex ->
                        val rowNovels = chunkedResults[chunkIndex]
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                NovelCard(novel = rowNovels[0], onClick = { onBookClick(rowNovels[0].id) })
                            }
                            
                            if (rowNovels.size > 1) {
                                Box(modifier = Modifier.weight(1f)) {
                                    NovelCard(novel = rowNovels[1], onClick = { onBookClick(rowNovels[1].id) })
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
            state.searchQuery.isNotBlank() -> {
                // No search results
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No books found for \"${state.searchQuery}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                }
            }
            else -> {
                // Empty state when no novels and no search query
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No novels available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                }
            }
        }
    }
}
