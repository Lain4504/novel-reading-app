package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.model.NovelStatus
import com.miraimagiclab.novelreadingapp.ui.components.ErrorState
import com.miraimagiclab.novelreadingapp.ui.components.NovelCard
import com.miraimagiclab.novelreadingapp.ui.components.StatsCard
import com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary
import com.miraimagiclab.novelreadingapp.ui.viewmodel.BookListViewModel
import com.miraimagiclab.novelreadingapp.util.UiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookListScreen(
    onBookClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: BookListViewModel = hiltViewModel(),
    sessionManager: SessionManager
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val authState by sessionManager.authState.collectAsState()
    
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshData() }
    )
    
    // Show login prompt if not logged in
    if (!authState.isLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App branding
            Text(
                text = "📚",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Novel Reading App",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Welcome message
            Text(
                text = "Your Book Collection",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Please login to access your book list and reading progress",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Beautiful login button
            Button(
                onClick = { onLoginClick() },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Login to Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Additional info
            Text(
                text = "Track your favorite novels and continue where you left off",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
        return
    }
    
    // Show content when logged in
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        when (val currentState = uiState) {
            is UiState.Idle, is UiState.Loading -> {
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
                BookListContent(
                    followingNovels = currentState.data.novels,
                    readingHistoryNovels = currentState.data.readingHistoryNovels,
                    onBookClick = onBookClick,
                    onBackClick = onBackClick,
                    onDeleteNovel = { novelId -> viewModel.deleteNovel(novelId) }
                )
            }
        }
        
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookListContent(
    followingNovels: List<Novel>,
    readingHistoryNovels: List<Novel>,
    onBookClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onDeleteNovel: (String) -> Unit
) {

    // Tab state
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Reading History", "Following")
    
    // Drag and Drop state
    var draggedNovelId by remember { mutableStateOf<String?>(null) }
    var deleteButtonPosition by remember { mutableStateOf(Offset.Zero) }
    var deleteButtonSize by remember { mutableStateOf(IntSize.Zero) }
    var isHoveringDeleteButton by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var novelToDelete by remember { mutableStateOf<String?>(null) }

    // Get novels for currently selected tab
    val currentNovels = when (selectedTabIndex) {
        0 -> readingHistoryNovels
        1 -> followingNovels
        else -> emptyList()
    }
    

    // Delete confirmation dialog
    if (showDeleteDialog && novelToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                novelToDelete = null
            },
            title = { Text("Xóa Novel") },
            text = { Text("Bạn có chắc chắn muốn xóa novel này khỏi danh sách của bạn?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        novelToDelete?.let { onDeleteNovel(it) }
                        showDeleteDialog = false
                        novelToDelete = null
                    }
                ) {
                    Text("Đồng ý")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        novelToDelete = null
                    }
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top bar with Delete button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "My Library",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            
            // Delete Button (Drop Zone)
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        deleteButtonPosition = coordinates.positionInRoot()
                        deleteButtonSize = coordinates.size
                    }
            ) {
                IconButton(
                    onClick = { /* No direct click action */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = if (isHoveringDeleteButton) 
                            MaterialTheme.colorScheme.error 
                        else 
                            androidx.compose.ui.graphics.Color(0xFF118B50)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))


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

        Spacer(modifier = Modifier.height(16.dp))

        // Book grid (2 columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(currentNovels, key = { it.id }) { book ->
                Box(
                    modifier = Modifier
                        .pointerInput(book.id) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    draggedNovelId = book.id
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val currentPosition = change.position
                                    
                                    // Check if hovering over delete button
                                    val isOver = currentPosition.x + deleteButtonPosition.x in 
                                        deleteButtonPosition.x..(deleteButtonPosition.x + deleteButtonSize.width) &&
                                        currentPosition.y in 
                                        deleteButtonPosition.y..(deleteButtonPosition.y + deleteButtonSize.height)
                                    
                                    isHoveringDeleteButton = isOver
                                },
                                onDragEnd = {
                                    if (isHoveringDeleteButton && draggedNovelId != null) {
                                        novelToDelete = draggedNovelId
                                        showDeleteDialog = true
                                    }
                                    draggedNovelId = null
                                    isHoveringDeleteButton = false
                                },
                                onDragCancel = {
                                    draggedNovelId = null
                                    isHoveringDeleteButton = false
                                }
                            )
                        }
                ) {
                    NovelCard(
                        novel = book,
                        onClick = { 
                            if (draggedNovelId == null) {
                                onBookClick(book.id) 
                            }
                        }
                    )
                }
            }
        }
    }
}
