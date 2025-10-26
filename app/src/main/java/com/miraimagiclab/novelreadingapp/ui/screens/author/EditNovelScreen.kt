package com.miraimagiclab.novelreadingapp.ui.screens.author

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.ui.viewmodel.AuthorViewModel
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNovelScreen(
    novelId: String,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AuthorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var isR18 by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("DRAFT") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedCoverUri by remember { mutableStateOf<Uri?>(null) }
    var coverImageUrl by remember { mutableStateOf<String?>(null) }
    var existingCoverUrl by remember { mutableStateOf<String?>(null) }
    
    val uiState by viewModel.uiState.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val uploadImageState by viewModel.uploadImageState.collectAsState()
    val selectedNovel by viewModel.selectedNovel.collectAsState()
    
    // Handle upload image success
    LaunchedEffect(uploadImageState) {
        val currentState = uploadImageState
        if (currentState is com.miraimagiclab.novelreadingapp.util.UiState.Success) {
            coverImageUrl = currentState.data
            selectedCoverUri = null
            viewModel.resetUploadImageState()
            
            // After successful upload, update the novel with the new cover URL
            viewModel.updateNovel(
                novelId = novelId,
                title = title,
                description = description,
                authorName = authorName,
                authorId = authState.userId,
                categories = selectedCategories,
                status = status,
                isR18 = isR18,
                coverImageUrl = coverImageUrl
            )
        }
    }
    
    // Cover image picker
    val coverImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedCoverUri = it
            // Don't upload immediately, just store the URI
        }
    }

    // Load novel data
    LaunchedEffect(novelId) {
        viewModel.loadNovelById(novelId)
    }

    // Populate form when novel data is loaded
    LaunchedEffect(selectedNovel) {
        selectedNovel?.let { novel ->
            title = novel.title
            description = novel.description
            authorName = novel.authorName
            selectedCategories = novel.categories.toSet()
            isR18 = novel.isR18
            status = novel.status
            existingCoverUrl = novel.coverImage
        }
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
                },
                windowInsets = WindowInsets(0)
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

            // Cover Image Section
            Text(
                text = "Cover Image",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cover preview
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedCoverUri != null -> {
                            AsyncImage(
                                model = selectedCoverUri,
                                contentDescription = "Cover Image",
                                modifier = Modifier.fillMaxSize()
                            )
                            // Remove button
                            IconButton(
                                onClick = {
                                    selectedCoverUri = null
                                    coverImageUrl = null
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        existingCoverUrl != null -> {
                            AsyncImage(
                                model = existingCoverUrl,
                                contentDescription = "Current Cover",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFFCCCCCC),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                // Pick button
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { coverImageLauncher.launch("image/*") },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Change Cover", fontWeight = FontWeight.Medium)
                    }
                    
                    if (selectedCoverUri != null) {
                        Text(
                            text = "New image selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

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
                        // If new image is selected, upload it first
                        if (selectedCoverUri != null) {
                            val file = uriToFile(context, selectedCoverUri!!)
                            if (file != null) {
                                viewModel.uploadImage(file, novelId, "NOVEL")
                            }
                        } else {
                            // No new image, update with existing cover URL
                            viewModel.updateNovel(
                                novelId = novelId,
                                title = title,
                                description = description,
                                authorName = authorName,
                                authorId = authState.userId,
                                categories = selectedCategories,
                                status = status,
                                isR18 = isR18,
                                coverImageUrl = existingCoverUrl
                            )
                        }
                    }
                },
                enabled = !uiState.isLoading && uploadImageState !is com.miraimagiclab.novelreadingapp.util.UiState.Loading && title.isNotBlank() && description.isNotBlank() && authorName.isNotBlank() && selectedCategories.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading || uploadImageState is com.miraimagiclab.novelreadingapp.util.UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (uploadImageState is com.miraimagiclab.novelreadingapp.util.UiState.Loading) "Uploading..." else "Updating...")
                } else {
                    Text("Update Novel")
                }
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
