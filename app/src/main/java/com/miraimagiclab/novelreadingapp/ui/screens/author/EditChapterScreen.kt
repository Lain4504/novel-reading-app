package com.miraimagiclab.novelreadingapp.ui.screens.author

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.ui.viewmodel.AuthorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChapterScreen(
    novelId: String,
    chapterId: String,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AuthorViewModel = hiltViewModel()
) {
    var chapterTitle by remember { mutableStateOf("") }
    var chapterNumber by remember { mutableStateOf(1) }
    var content by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()

    // Load chapter data
    LaunchedEffect(chapterId) {
        // TODO: Load chapter details and populate form
        // For now, we'll use placeholder values
    }

    LaunchedEffect(uiState.updatedChapter) {
        uiState.updatedChapter?.let {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Chapter") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Chapter Number
            OutlinedTextField(
                value = chapterNumber.toString(),
                onValueChange = { 
                    it.toIntOrNull()?.let { num ->
                        chapterNumber = num
                    }
                },
                label = { Text("Chapter Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Chapter Title
            OutlinedTextField(
                value = chapterTitle,
                onValueChange = { chapterTitle = it },
                label = { Text("Chapter Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Content
            Text(
                text = "Content",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Chapter content...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                maxLines = 20
            )

            // Word count
            Text(
                text = "Word count: ${content.split("\\s+".toRegex()).filter { it.isNotBlank() }.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Update Button
            Button(
                onClick = {
                    if (chapterTitle.isNotBlank() && content.isNotBlank()) {
                        viewModel.updateChapter(
                            novelId = novelId,
                            chapterId = chapterId,
                            chapterTitle = chapterTitle,
                            chapterNumber = chapterNumber,
                            content = content
                        )
                    }
                },
                enabled = !uiState.isLoading && chapterTitle.isNotBlank() && content.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Update Chapter")
            }

            // Error message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Chapter") },
                text = { Text("Are you sure you want to delete this chapter? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteChapter(chapterId)
                            showDeleteDialog = false
                            onBackClick()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
