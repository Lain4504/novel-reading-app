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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
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


@Composable
fun ExploreScreen(
    onBookClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    
    when (val currentState = uiState) {
            is UiState.Idle, is UiState.Loading -> {
                // Show loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                ErrorState(
                    message = currentState.message,
                    onRetry = { viewModel.refreshData() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            is UiState.Success -> {
            ExploreContentSimple(
                exploreData = currentState.data,
                onBookClick = onBookClick,
                onBackClick = onBackClick,
                scrollState = scrollState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreContentSimple(
    exploreData: com.miraimagiclab.novelreadingapp.ui.viewmodel.ExploreUiState,
    onBookClick: (String) -> Unit,
    onBackClick: () -> Unit,
    scrollState: androidx.compose.foundation.ScrollState
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    // Filter logic
    fun matchesFilter(book: Novel): Boolean {
        val filterOk = when (selectedFilter) {
            "All" -> true
            else -> true
        }
        val queryOk = query.isBlank() ||
                book.title.contains(query, ignoreCase = true) ||
                book.authorName.contains(query, ignoreCase = true)
        return filterOk && queryOk
    }

    val recommended = remember(exploreData, query, selectedFilter) {
        exploreData.recommendedNovels.filter { matchesFilter(it) }
    }
    val ourPicks = remember(exploreData, query, selectedFilter) {
        exploreData.ourPicksNovels.filter { matchesFilter(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
                    imageVector = Icons.Default.ArrowBack,
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

        // Search Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                "Search...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recommended Section
        if (recommended.isNotEmpty()) {
            Text(
                text = "Recommended for you",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(horizontal = Spacing.xs)
            ) {
                items(recommended) { book ->
                    NovelCard(novel = book, onClick = { onBookClick(book.id) })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Our Picks Section
        if (ourPicks.isNotEmpty()) {
            Text(
                text = "Our Picks",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(horizontal = Spacing.xs)
            ) {
                items(ourPicks) { book ->
                    NovelCard(novel = book, onClick = { onBookClick(book.id) })
                }
            }
        }
    }
}

@Composable
private fun ExploreContent(
    exploreData: com.miraimagiclab.novelreadingapp.ui.viewmodel.ExploreUiState,
    searchQuery: String,
    searchResults: List<Novel>,
    isSearching: Boolean,
    isLoadingMore: Boolean,
    hasMorePages: Boolean,
    onBookClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLoadMore: () -> Unit,
    onUpdateSort: (String, String) -> Unit,
    scrollState: androidx.compose.foundation.ScrollState
) {
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Latest Updated") }
    
    val listState = rememberLazyListState()
    
    // Apply filter function
    fun applyFilter(filterOption: String, onUpdateSort: (String, String) -> Unit) {
        val (sortBy, sortDirection) = when (filterOption) {
            "Latest Updated" -> "updatedAt" to "desc"
            "Oldest Updated" -> "updatedAt" to "asc"
            "Latest Created" -> "createdAt" to "desc"
            "Oldest Created" -> "createdAt" to "asc"
            else -> "updatedAt" to "desc"
        }
        onUpdateSort(sortBy, sortDirection)
    }
    
    // Infinite scroll detection
    LaunchedEffect(listState, hasMorePages, isLoadingMore) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && hasMorePages && !isLoadingMore) {
                    val lastVisibleItem = visibleItems.last()
                    val totalItems = listState.layoutInfo.totalItemsCount
                    
                    // Load more when user scrolls to last 3 items
                    if (lastVisibleItem.index >= totalItems - 3) {
                        onLoadMore()
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        // 🔙 Top bar with Back + Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
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

        // 🔍 Search + Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Custom Search Box
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
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "Search...",
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
                        imageVector = Icons.Default.List,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
                ) {
                    listOf("Latest Updated", "Oldest Updated", "Latest Created", "Oldest Created").forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    fontWeight = if (selectedFilter == option)
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedFilter = option
                                showFilterMenu = false
                                // Apply filter based on selection
                                applyFilter(option, onUpdateSort)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show all novels with search functionality
        if (isSearching) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else if (searchResults.isNotEmpty()) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(vertical = Spacing.sm)
            ) {
                // Group search results into pairs (chunks of 2)
                val chunkedResults = searchResults.chunked(2)
                items(chunkedResults.size) { chunkIndex ->
                    val rowNovels = chunkedResults[chunkIndex]
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // First item in the row
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            NovelCard(novel = rowNovels[0], onClick = { onBookClick(rowNovels[0].id) })
                        }
                        
                        // Second item in the row (if exists)
                        if (rowNovels.size > 1) {
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                NovelCard(novel = rowNovels[1], onClick = { onBookClick(rowNovels[1].id) })
                            }
                        } else {
                            // Empty space to maintain consistent layout
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                
                // Load more indicator
                if (isLoadingMore) {
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
        } else {
            // No results found
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotBlank()) {
                        "No books found for \"$searchQuery\""
                    } else {
                        "No books available"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }
        }
    }
}
