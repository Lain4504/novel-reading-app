package com.miraimagiclab.novelreadingapp.ui.screens.author

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.miraimagiclab.novelreadingapp.data.remote.dto.NovelDto
import com.miraimagiclab.novelreadingapp.ui.viewmodel.AuthorViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorDashboardScreen(
    onBackClick: () -> Unit,
    onCreateNovelClick: () -> Unit,
    onNovelClick: (String) -> Unit,
    viewModel: AuthorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val authorNovels by viewModel.authorNovels.collectAsState()
    val authState by viewModel.authState.collectAsState()
    
    // Delete dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var novelToDelete by remember { mutableStateOf<NovelDto?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        authState.userId?.let { userId ->
            viewModel.loadAuthorNovels(userId)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("My Novels") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets(0)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNovelClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Novel")
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            authorNovels.isEmpty() -> {
                EmptyNovelsState(
                    onCreateNovelClick = onCreateNovelClick,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Novel cards
                    items(authorNovels) { novel ->
                        NovelCard(
                            novel = novel,
                            onClick = { onNovelClick(novel.id) },
                            onLongClick = {
                                novelToDelete = novel
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Error message
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // Show snackbar or error dialog
            }
        }
        
        // Delete confirmation dialog
        novelToDelete?.let { novel ->
            if (showDeleteDialog) {
                DeleteConfirmDialog(
                    novelTitle = novel.title,
                    onConfirm = {
                        viewModel.deleteNovel(novel.id)
                        showDeleteDialog = false
                        novelToDelete = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Novel deleted successfully")
                        }
                    },
                    onDismiss = {
                        showDeleteDialog = false
                        novelToDelete = null
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyNovelsState(
    onCreateNovelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Novels Yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Start your writing journey by creating your first novel",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onCreateNovelClick) {
            Text("Create Your First Novel")
        }
    }
}

@Composable
private fun NovelCard(
    novel: NovelDto,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cover image
            AsyncImage(
                model = novel.coverImage ?: "https://via.placeholder.com/80x120",
                contentDescription = "Novel cover",
                modifier = Modifier
                    .size(80.dp, 120.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            
            // Novel info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title
                Text(
                    text = novel.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Status with colored badge
                StatusChip(status = novel.status)
                
                // Additional info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${novel.chapterCount} chapters",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "${novel.viewCount} views",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Arrow indicator
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val statusColor = when (status.uppercase()) {
        "PUBLISHED" -> MaterialTheme.colorScheme.primary
        "ONGOING" -> MaterialTheme.colorScheme.tertiary
        "COMPLETED" -> MaterialTheme.colorScheme.secondary
        "DRAFT" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.outline
    }
    
    Surface(
        shape = MaterialTheme.shapes.small,
        color = statusColor.copy(alpha = 0.1f)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = statusColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DeleteConfirmDialog(
    novelTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Novel?") },
        text = { Text("Are you sure you want to delete \"$novelTitle\"? This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm, 
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
