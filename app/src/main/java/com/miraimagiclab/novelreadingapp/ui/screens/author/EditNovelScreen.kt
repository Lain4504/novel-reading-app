package com.miraimagiclab.novelreadingapp.ui.screens.author

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.ui.viewmodel.AuthorViewModel
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNovelScreen(
    novelId: String,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AuthorViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var isR18 by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("DRAFT") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    val authState by viewModel.authState.collectAsState()

    // Load novel data
    LaunchedEffect(novelId) {
        // TODO: Load novel details and populate form
        // For now, we'll use placeholder values
    }

    LaunchedEffect(uiState.updatedNovel) {
        uiState.updatedNovel?.let {
            onSuccess()
        }
    }

    val categories = listOf(
        "FANTASY", "ROMANCE", "MYSTERY", "THRILLER", "SCIENCE_FICTION",
        "HORROR", "ADVENTURE", "DRAMA", "COMEDY", "HISTORICAL"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Novel") },
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
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Author Name
            OutlinedTextField(
                value = authorName,
                onValueChange = { authorName = it },
                label = { Text("Author Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            // Categories
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        onClick = {
                            selectedCategories = if (selectedCategories.contains(category)) {
                                selectedCategories - category
                            } else {
                                selectedCategories + category
                            }
                        },
                        label = { Text(category) },
                        selected = selectedCategories.contains(category)
                    )
                }
            }

            // Status
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            val statusOptions = listOf("DRAFT", "PUBLISHED", "COMPLETED")
            statusOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = status == option,
                            onClick = { status = option }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = status == option,
                        onClick = { status = option }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(option)
                }
            }

            // R18 Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isR18,
                    onCheckedChange = { isR18 = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("R18 Content")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Update Button
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && authorName.isNotBlank() && selectedCategories.isNotEmpty()) {
                        viewModel.updateNovel(
                            novelId = novelId,
                            title = title,
                            description = description,
                            authorName = authorName,
                            authorId = authState.userId,
                            categories = selectedCategories,
                            status = status,
                            isR18 = isR18
                        )
                    }
                },
                enabled = !uiState.isLoading && title.isNotBlank() && description.isNotBlank() && authorName.isNotBlank() && selectedCategories.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Update Novel")
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
                title = { Text("Delete Novel") },
                text = { Text("Are you sure you want to delete this novel? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteNovel(novelId)
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
