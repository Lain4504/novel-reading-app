package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.domain.model.*
import com.miraimagiclab.novelreadingapp.ui.components.novel.*
import com.miraimagiclab.novelreadingapp.ui.viewmodel.NovelDetailViewModel
import com.miraimagiclab.novelreadingapp.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovelDetailScreen(
    bookId: String,
    onBackClick: () -> Unit,
    onChapterClick: (chapter: Chapter) -> Unit = {},
    onNovelClick: (novelId: String) -> Unit = {},
    onNavigateToComments: (novelId: String) -> Unit = {},
    onNavigateToCreateReview: (novelId: String) -> Unit = {},
    viewModel: NovelDetailViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    // Load novel detail when screen is first displayed
    LaunchedEffect(bookId) {
        viewModel.loadNovelDetail(bookId)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    when (val currentState = uiState) {
        is UiState.Idle -> {
            // Show skeleton loading state
            NovelDetailSkeleton(
                onBackClick = onBackClick
            )
        }
        is UiState.Loading -> {
            // Show skeleton loading state
            NovelDetailSkeleton(
                onBackClick = onBackClick
            )
        }
        is UiState.Error -> {
            NovelDetailErrorState(
                errorMessage = currentState.message,
                onRetry = { viewModel.refreshData() },
                onBackClick = onBackClick
            )
        }
        is UiState.Success -> {
            val novelDetail = currentState.data
            NovelDetailContent(
                novelDetail = novelDetail,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                onBackClick = onBackClick,
                onChapterClick = onChapterClick,
                onNovelClick = onNovelClick,
                onNavigateToComments = onNavigateToComments,
                onNavigateToCreateReview = onNavigateToCreateReview,
                viewModel = viewModel
            )
        }
    }
}