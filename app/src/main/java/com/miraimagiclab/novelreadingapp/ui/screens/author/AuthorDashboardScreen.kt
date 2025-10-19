package com.miraimagiclab.novelreadingapp.ui.screens.author

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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

    LaunchedEffect(Unit) {
        authState.userId?.let { userId ->
            viewModel.loadAuthorNovels(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Novels") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
                    items(authorNovels) { novel ->
                        NovelItem(
                            novel = novel,
                            onClick = { onNovelClick(novel.id) }
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
private fun NovelItem(
    novel: NovelDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = novel.coverImage ?: "https://via.placeholder.com/80x120",
                contentDescription = "Novel cover",
                modifier = Modifier.size(80.dp, 120.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = novel.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = novel.description.take(100) + if (novel.description.length > 100) "..." else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Chapters: ${novel.chapterCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Status: ${novel.status}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
