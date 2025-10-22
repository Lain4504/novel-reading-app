package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
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
import androidx.navigation.NavController
import com.miraimagiclab.novelreadingapp.domain.model.Novel
import com.miraimagiclab.novelreadingapp.domain.model.NovelStatus
import com.miraimagiclab.novelreadingapp.navigation.Screen
import com.miraimagiclab.novelreadingapp.ui.components.BookCard
import com.miraimagiclab.novelreadingapp.ui.components.BottomNavigationBar
import com.miraimagiclab.novelreadingapp.ui.components.ErrorState
import com.miraimagiclab.novelreadingapp.ui.viewmodel.InProgressViewModel
import com.miraimagiclab.novelreadingapp.util.UiState

@Composable
fun InProgressScreen(
    navController: NavController,
    onBookClick: (String) -> Unit = {},
    viewModel: InProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.InProgress.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val currentState = uiState) {
            is UiState.Idle, is UiState.Loading -> {
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
                ErrorState(
                    message = currentState.message,
                    onRetry = { viewModel.refreshData() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is UiState.Success -> {
                InProgressContent(
                    novels = currentState.data.novels,
                    navController = navController,
                    onBookClick = onBookClick,
                    innerPadding = innerPadding,
                    onDeleteNovel = { novelId -> viewModel.deleteNovel(novelId) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InProgressContent(
    novels: List<Novel>,
    navController: NavController,
    onBookClick: (String) -> Unit,
    innerPadding: PaddingValues,
    onDeleteNovel: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    
    // Drag and Drop state
    var draggedNovelId by remember { mutableStateOf<String?>(null) }
    var deleteButtonPosition by remember { mutableStateOf(Offset.Zero) }
    var deleteButtonSize by remember { mutableStateOf(IntSize.Zero) }
    var isHoveringDeleteButton by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var novelToDelete by remember { mutableStateOf<String?>(null) }

    // Filter logic
    fun matchesFilter(book: Novel): Boolean {
        val filterOk = when (selectedFilter) {
            "All" -> true
            "Novel" -> book.status.name == "NOVEL"
            "Light Novel" -> book.status.name == "LIGHT_NOVEL"
            "Manga" -> book.status.name == "MANGA"
            else -> true
        }
        val queryOk = query.isBlank() ||
                book.title.contains(query, ignoreCase = true) ||
                book.authorName.contains(query, ignoreCase = true)
        return filterOk && queryOk
    }

    val filteredBooks = remember(novels, query, selectedFilter) {
        novels.filter { matchesFilter(it) }
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
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
            // 🔙 Top bar with Delete button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.navigate(Screen.BookList.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "In Progress",
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

            // 🔍 Search + Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search box
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

                // Filter button
                Box {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        listOf("All", "Novel", "Light Novel", "Manga").forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option,
                                        fontWeight = if (selectedFilter == option) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedFilter = option
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📚 Book grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(filteredBooks, key = { it.id }) { book ->
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
                        BookCard(
                            book = book,
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

