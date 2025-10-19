package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.ui.components.ReadingSettingsDialog
import com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary
import com.miraimagiclab.novelreadingapp.ui.theme.ReadingThemes
import com.miraimagiclab.novelreadingapp.ui.theme.getFontFamilyByName
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ReadingSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(
    chapterTitle: String,
    chapterContent: String,
    onBackClick: () -> Unit,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
    hasPreviousChapter: Boolean = true,
    hasNextChapter: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: ReadingSettingsViewModel = hiltViewModel()
) {
    var showUI by remember { mutableStateOf(false) }
    var showChapterList by remember { mutableStateOf(false) }
    var showReadingSettings by remember { mutableStateOf(false) }

    // Collect reading settings
    val fontFamily by viewModel.fontFamily.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val lineSpacing by viewModel.lineSpacing.collectAsState()
    val readingTheme by viewModel.readingTheme.collectAsState()

    // Get current reading theme
    val currentTheme = ReadingThemes.getThemeByName(readingTheme)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(currentTheme.backgroundColor)
            .clickable { showUI = !showUI }
    ) {
        // Top App Bar - Only show when showUI is true
        if (showUI) {
            TopAppBar(
                title = {
                    Text(
                        text = chapterTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                 actions = {
                     // Empty actions - icons moved to bottom navigation
                 },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                windowInsets = WindowInsets(0),
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // Green accent line - Only show when showUI is true
        if (showUI) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(GreenPrimary)
                    .align(Alignment.TopCenter)
            )
        }

        // Main content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(currentTheme.backgroundColor)
        ) {
            // Chapter content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = chapterContent,
                    color = currentTheme.textColor,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = getFontFamilyByName(fontFamily),
                        lineHeight = (fontSize * lineSpacing).sp,
                        fontSize = fontSize.sp
                    ),
                    textAlign = TextAlign.Justify
                )
            }

            // Bottom navigation bar - Only show when showUI is true
            if (showUI) {
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                     // Bottom navigation bar
                     Row(
                         modifier = Modifier
                             .fillMaxWidth()
                             .background(MaterialTheme.colorScheme.surface)
                             .padding(16.dp),
                         horizontalArrangement = Arrangement.SpaceBetween,
                         verticalAlignment = Alignment.CenterVertically
                     ) {
                         // Previous button
                         Button(
                             onClick = onPreviousChapter,
                             enabled = hasPreviousChapter,
                             colors = ButtonDefaults.buttonColors(
                                 containerColor = Color.Transparent,
                                 contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                             ),
                             border = androidx.compose.foundation.BorderStroke(
                                 1.dp,
                                 MaterialTheme.colorScheme.outline
                             ),
                             shape = RoundedCornerShape(8.dp),
                             modifier = Modifier.height(48.dp)
                         ) {
                             Row(
                                 verticalAlignment = Alignment.CenterVertically,
                                 horizontalArrangement = Arrangement.spacedBy(8.dp)
                             ) {
                                 Icon(
                                     imageVector = Icons.Default.ArrowBack,
                                     contentDescription = "Previous",
                                     modifier = Modifier.size(18.dp)
                                 )
                                 Text(
                                     text = "Previous",
                                     style = MaterialTheme.typography.bodyMedium
                                 )
                             }
                         }

                         // Center icons row
                         Row(
                             horizontalArrangement = Arrangement.spacedBy(12.dp),
                             verticalAlignment = Alignment.CenterVertically
                         ) {
                             // Book Details Icon
                             IconButton(onClick = onBackClick) {
                                 Icon(
                                     imageVector = Icons.Default.Settings,
                                     contentDescription = "Book Details",
                                     tint = MaterialTheme.colorScheme.onSurface,
                                     modifier = Modifier.size(24.dp)
                                 )
                             }
                             
                             // Chapter List Icon
                             IconButton(onClick = { showChapterList = !showChapterList }) {
                                 Icon(
                                     imageVector = Icons.Default.List,
                                     contentDescription = "Chapter List",
                                     tint = MaterialTheme.colorScheme.onSurface,
                                     modifier = Modifier.size(24.dp)
                                 )
                             }
                             
                             // Settings Icon
                             IconButton(onClick = { showReadingSettings = true }) {
                                 Icon(
                                     imageVector = Icons.Default.Settings,
                                     contentDescription = "Reading Settings",
                                     tint = MaterialTheme.colorScheme.onSurface,
                                     modifier = Modifier.size(24.dp)
                                 )
                             }
                         }

                         // Next button
                         Button(
                             onClick = onNextChapter,
                             enabled = hasNextChapter,
                             colors = ButtonDefaults.buttonColors(
                                 containerColor = GreenPrimary,
                                 contentColor = Color.White
                             ),
                             shape = RoundedCornerShape(8.dp),
                             modifier = Modifier.height(48.dp)
                         ) {
                             Row(
                                 verticalAlignment = Alignment.CenterVertically,
                                 horizontalArrangement = Arrangement.spacedBy(8.dp)
                             ) {
                                 Text(
                                     text = "Next",
                                     style = MaterialTheme.typography.bodyMedium
                                 )
                                 Icon(
                                     imageVector = Icons.Default.ArrowBack,
                                     contentDescription = "Next",
                                     modifier = Modifier.size(18.dp)
                                 )
                             }
                         }
                     }
                }
            }

            // Chapter List Overlay
            if (showChapterList) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showChapterList = false }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .fillMaxHeight(0.7f)
                            .align(Alignment.Center),
                        colors = CardDefaults.cardColors(
                            containerColor = currentTheme.surfaceColor
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Chapter List",
                                style = MaterialTheme.typography.headlineSmall,
                                color = currentTheme.onSurfaceColor,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Simple chapter list - you can expand this with actual chapter data
                            repeat(7) { index ->
                                Text(
                                    text = "Chapter ${index + 1}: Sample Chapter Title",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = currentTheme.onSurfaceColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showChapterList = false
                                            // Handle chapter selection
                                        }
                                        .padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Reading Settings Dialog
            if (showReadingSettings) {
                ReadingSettingsDialog(
                    viewModel = viewModel,
                    onDismiss = { showReadingSettings = false }
                )
            }
        }
    }
}
