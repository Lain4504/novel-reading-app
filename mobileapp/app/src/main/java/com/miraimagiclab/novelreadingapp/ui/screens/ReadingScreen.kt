package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.miraimagiclab.novelreadingapp.ui.components.ReadingSettingsDialog
import com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary
import com.miraimagiclab.novelreadingapp.ui.theme.ReadingThemes
import com.miraimagiclab.novelreadingapp.ui.theme.getFontFamilyByName
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ReadingSettingsViewModel
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ReadingProgressViewModel
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ReadingScreenViewModel
import com.miraimagiclab.novelreadingapp.util.UiState
import com.miraimagiclab.novelreadingapp.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(
    novelId: String,
    chapterId: String,
    onBackClick: () -> Unit,
    onNavigateToNovelDetail: (String) -> Unit,
    sessionManager: com.miraimagiclab.novelreadingapp.data.auth.SessionManager,
    modifier: Modifier = Modifier,
    viewModel: ReadingSettingsViewModel = hiltViewModel(),
    progressViewModel: ReadingProgressViewModel = hiltViewModel(),
    readingViewModel: ReadingScreenViewModel = hiltViewModel()
) {
    var showUI by remember { mutableStateOf(false) }
    var showChapterList by remember { mutableStateOf(false) }
    var showReadingSettings by remember { mutableStateOf(false) }

    // Haptic feedback
    val hapticFeedback = rememberHapticFeedback()
    
    // Collect auth state to get real user ID
    val authState by sessionManager.authState.collectAsState()
    val userId = authState.userId ?: ""

    // Collect reading settings
    val fontFamily by viewModel.fontFamily.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val lineSpacing by viewModel.lineSpacing.collectAsState()
    val readingTheme by viewModel.readingTheme.collectAsState()
    
    // Collect progress tracking state
    val currentProgress by progressViewModel.currentProgress.collectAsState()
    val isLoadingProgress by progressViewModel.isLoading.collectAsState()
    val progressError by progressViewModel.error.collectAsState()
    
    // Collect chapter data from ViewModel
    val currentChapterState by readingViewModel.currentChapter.collectAsState()
    val chapterListState by readingViewModel.chapterList.collectAsState()
    val isLoading by readingViewModel.isLoading.collectAsState()
    val error by readingViewModel.error.collectAsState()
    
    // Load chapter data when screen is first displayed
    LaunchedEffect(novelId, chapterId) {
        readingViewModel.loadChapter(novelId, chapterId)
    }

    // Get current reading theme
    val currentTheme = ReadingThemes.getThemeByName(readingTheme)
    
    // Load reading progress when screen is first displayed
    LaunchedEffect(novelId) {
        progressViewModel.getReadingProgress(userId, novelId)
    }
    
    // Update reading progress when chapter data is successfully loaded
    LaunchedEffect(currentChapterState) {
        when (val state = currentChapterState) {
            is UiState.Success -> {
                // Extract chapter number from chapter data or use a default
                val chapterNumber = state.data.chapterNumber ?: 1
                println("DEBUG: ReadingScreen updating progress - userId: $userId, novelId: $novelId, chapterId: $chapterId, chapterNumber: $chapterNumber")
                progressViewModel.updateReadingProgress(userId, novelId, chapterId, chapterNumber)
            }
            else -> { 
                println("DEBUG: ReadingScreen not updating progress - state is not Success: $state")
            }
        }
    }

    // Enhanced navigation functions with loading states
    fun handlePreviousChapter() {
        if (!isLoading && readingViewModel.hasPreviousChapter()) {
            hapticFeedback.light()
            readingViewModel.navigateToPreviousChapter()
        }
    }

    fun handleNextChapter() {
        if (!isLoading && readingViewModel.hasNextChapter()) {
            hapticFeedback.light()
            readingViewModel.navigateToNextChapter()
        }
    }

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
                    val state = currentChapterState
                    Text(
                        text = when (state) {
                            is UiState.Success -> state.data.chapterTitle
                            is UiState.Error -> "Error"
                            else -> "Loading..."
                        },
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
            // Reading progress indicator
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val progressIndicatorHeight = 3.dp
            
            // Chapter progress indicator at top
            val isChapterCompleted = currentProgress?.isCompleted ?: false
            val animatedProgress by animateFloatAsState(
                targetValue = if (isChapterCompleted) 1f else 0f,
                animationSpec = tween(durationMillis = 500),
                label = "chapter_progress"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(progressIndicatorHeight)
                    .background(currentTheme.backgroundColor)
                    .align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(screenWidth * animatedProgress)
                        .background(GreenPrimary)
                )
            }

            // Chapter content with enhanced typography
            val scrollState = rememberScrollState()
            val responsiveFontSize = remember(fontSize, configuration) {
                // Responsive font sizing based on screen dimensions
                val baseFontSize = fontSize
                val screenDensity = configuration.densityDpi / 160f
                val adjustedSize = baseFontSize * (1 + (screenDensity - 1) * 0.1f)
                adjustedSize.coerceIn(12f, 28f)
            }

            // Loading overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .align(Alignment.Center)
                ) {
                    Card(
                        modifier = Modifier
                            .padding(32.dp)
                            .align(Alignment.Center),
                        colors = CardDefaults.cardColors(
                            containerColor = currentTheme.surfaceColor
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = GreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading chapter...",
                                color = currentTheme.onSurfaceColor,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .pointerInput(scrollState) {
                        // Scroll tracking removed - using chapter-level progress only
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                // Handle swipe gestures for navigation
                            }
                        ) { change, _ ->
                            // Detect horizontal swipes for chapter navigation
                            val horizontalDrag = change.position.x - change.previousPosition.x
                            if (kotlin.math.abs(horizontalDrag) > 50) { // Minimum swipe distance
                                if (horizontalDrag > 0 && readingViewModel.hasPreviousChapter()) {
                                    // Swipe right - previous chapter
                                    handlePreviousChapter()
                                } else if (horizontalDrag < 0 && readingViewModel.hasNextChapter()) {
                                    // Swipe left - next chapter
                                    handleNextChapter()
                                }
                            }
                        }
                    }
                    .padding(
                        horizontal = maxOf(16.dp, screenWidth * 0.05f),
                        vertical = 12.dp
                    )
            ) {
                // Add top padding to account for progress indicator
                Spacer(modifier = Modifier.height(progressIndicatorHeight + 8.dp))
                
                val state = currentChapterState
                when (state) {
                    is UiState.Success -> {
                        Text(
                            text = state.data.content,
                            color = currentTheme.textColor,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = getFontFamilyByName(fontFamily),
                                lineHeight = (responsiveFontSize * lineSpacing).sp,
                                fontSize = responsiveFontSize.sp,
                                letterSpacing = 0.5.sp
                            ),
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(
                                bottom = maxOf(16.dp, responsiveFontSize.dp * 0.5f)
                            )
                        )
                    }
                    is UiState.Error -> {
                        Text(
                            text = "Error loading chapter: ${state.message}",
                            color = currentTheme.textColor,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = GreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    else -> {
                        // Handle any other states
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = GreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
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
                            onClick = { handlePreviousChapter() },
                            enabled = readingViewModel.hasPreviousChapter(),
                             colors = ButtonDefaults.buttonColors(
                                 containerColor = if (readingViewModel.hasPreviousChapter()) Color.Transparent else Color.Transparent,
                                 contentColor = if (readingViewModel.hasPreviousChapter()) 
                                     MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                 else 
                                     MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                             ),
                             border = androidx.compose.foundation.BorderStroke(
                                 1.dp,
                                 if (readingViewModel.hasPreviousChapter()) 
                                     MaterialTheme.colorScheme.outline
                                 else 
                                     MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                             ),
                             shape = RoundedCornerShape(8.dp),
                             modifier = Modifier
                                 .height(48.dp)
                                 .animateContentSize()
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
                             // Novel Detail Icon
                             IconButton(
                                 onClick = { 
                                     hapticFeedback.light()
                                     onNavigateToNovelDetail(novelId)
                                 }
                             ) {
                                 Icon(
                                     imageVector = Icons.Default.Star,
                                     contentDescription = "Novel Details",
                                     tint = MaterialTheme.colorScheme.onSurface,
                                     modifier = Modifier.size(24.dp)
                                 )
                             }
                             
                             // Chapter List Icon
                             IconButton(
                                 onClick = { 
                                     hapticFeedback.light()
                                     showChapterList = !showChapterList 
                                 }
                             ) {
                                 Icon(
                                     imageVector = Icons.Default.List,
                                     contentDescription = "Chapter List",
                                     tint = MaterialTheme.colorScheme.onSurface,
                                     modifier = Modifier.size(24.dp)
                                 )
                             }
                             
                             // Settings Icon
                             IconButton(
                                 onClick = { 
                                     hapticFeedback.light()
                                     showReadingSettings = true 
                                 }
                             ) {
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
                            onClick = { handleNextChapter() },
                            enabled = readingViewModel.hasNextChapter(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (readingViewModel.hasNextChapter()) GreenPrimary else GreenPrimary.copy(alpha = 0.3f),
                                contentColor = Color.White
                            ),
                             shape = RoundedCornerShape(8.dp),
                             modifier = Modifier
                                 .height(48.dp)
                                 .animateContentSize()
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

                            // Chapter list from API
                            val listState = chapterListState
                            when (listState) {
                                is UiState.Success -> {
                                    LazyColumn {
                                        items(listState.data) { chapter ->
                                            Text(
                                                text = "Chapter ${chapter.chapterNumber}: ${chapter.chapterTitle}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (chapter.id == chapterId) 
                                                    GreenPrimary 
                                                else 
                                                    currentTheme.onSurfaceColor,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        showChapterList = false
                                                        readingViewModel.navigateToChapter(chapter.id)
                                                    }
                                                    .padding(vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                                is UiState.Error -> {
                                    Text(
                                        text = "Error loading chapters: ${listState.message}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = currentTheme.onSurfaceColor,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                is UiState.Loading -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = GreenPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                else -> {
                                    // Handle any other states
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = GreenPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
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
