package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.miraimagiclab.novelreadingapp.ui.components.NovelCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.ui.components.ErrorState
import com.miraimagiclab.novelreadingapp.ui.components.HomeScreenSkeleton
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing
import com.miraimagiclab.novelreadingapp.ui.viewmodel.HomeViewModel
import com.miraimagiclab.novelreadingapp.util.UiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AiRecommendationsScreen(
    onBackClick: () -> Unit,
    onNovelClick: (String) -> Unit,
    sessionManager: SessionManager,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val authState by sessionManager.authState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val aiTopic by viewModel.aiTopic.collectAsState()

    var showAssistant by remember { mutableStateOf(false) }
    var lastAskedTopic by remember { mutableStateOf<String?>(null) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshData() }
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Recommendations",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pullRefresh(pullRefreshState)
        ) {
            when (val state = uiState) {
                is UiState.Idle, is UiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Spacing.contentPadding)
                    ) {
                        // Topic input for AI-driven chat (available for all users)
                        var topicText by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = topicText,
                            onValueChange = { topicText = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Tell AI your topic") },
                            placeholder = { Text("e.g., cultivation fantasy, school life, horror") },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val asked = topicText.trim()
                                    viewModel.setAiTopic(asked.ifBlank { null })
                                    if (asked.isNotBlank()) {
                                        lastAskedTopic = asked
                                        showAssistant = true
                                    }
                                },
                                enabled = topicText.isNotBlank()
                            ) {
                                Text("Ask AI")
                            }
                            TextButton(onClick = {
                                topicText = ""
                                viewModel.setAiTopic(null)
                                showAssistant = false
                                lastAskedTopic = null
                            }) {
                                Text("Clear")
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.md))

                        // Chat-style conversation - only show after user taps "Ask AI"
                        if (showAssistant && (lastAskedTopic?.isNotBlank() == true)) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // User message hidden per request
                                Spacer(modifier = Modifier.height(0.dp))

                                // Assistant message hidden per request
                                Spacer(modifier = Modifier.height(0.dp))
                            }

                            Spacer(modifier = Modifier.height(Spacing.md))
                        }

                        // Chat-only view (no recommendations grid)
                        Spacer(modifier = Modifier.height(Spacing.sm))
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Spacing.contentPadding)
                    ) {
                        // Topic input remains available even on error (available for all users)
                        var topicText by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = topicText,
                            onValueChange = { topicText = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Tell AI your topic") },
                            placeholder = { Text("e.g., cultivation fantasy, school life, horror") },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val asked = topicText.trim()
                                    viewModel.setAiTopic(asked.ifBlank { null })
                                    if (asked.isNotBlank()) {
                                        lastAskedTopic = asked
                                        showAssistant = true
                                    }
                                },
                                enabled = topicText.isNotBlank()
                            ) {
                                Text("Ask AI")
                            }
                            TextButton(onClick = {
                                topicText = ""
                                viewModel.setAiTopic(null)
                                showAssistant = false
                                lastAskedTopic = null
                            }) {
                                Text("Clear")
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.md))

                        // Assistant error message bubble
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "AI",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Sorry, I couldn't get recommendations right now. Please try again.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.sm))
                    }
                }

                is UiState.Success -> {
                    val homeData = state.data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Spacing.contentPadding)
                    ) {
                        // Header

                        // Topic input for AI-driven recommendations (available for all users)
                        var topicText by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = topicText,
                            onValueChange = { topicText = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Tell AI your topic") },
                            placeholder = { Text("e.g., cultivation fantasy, school life, horror") },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val asked = topicText.trim()
                                    viewModel.setAiTopic(asked.ifBlank { null })
                                    if (asked.isNotBlank()) {
                                        lastAskedTopic = asked
                                        showAssistant = true
                                    }
                                },
                                enabled = topicText.isNotBlank()
                            ) {
                                Text("Ask AI")
                            }
                            TextButton(onClick = {
                                topicText = ""
                                viewModel.setAiTopic(null)
                                showAssistant = false
                                lastAskedTopic = null
                            }) {
                                Text("Clear")
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.md))

                        // Chat-style conversation - only show after user taps "Ask AI"
                        if (showAssistant && (lastAskedTopic?.isNotBlank() == true)) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // User message hidden per request
                                Spacer(modifier = Modifier.height(0.dp))

                                // Assistant message hidden per request
                                Spacer(modifier = Modifier.height(0.dp))
                            }

                            Spacer(modifier = Modifier.height(Spacing.md))
                        }

                        // AI recommendations section (cards like homepage)
                        if (homeData.recommendedNovels.isNotEmpty()) {
                            if (lastAskedTopic?.isNotBlank() == true) {
                                Text(
                                    text = "AI recommend for " + lastAskedTopic,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = Spacing.md)
                                )
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                                contentPadding = PaddingValues(horizontal = Spacing.xs)
                            ) {
                                items(homeData.recommendedNovels) { novel ->
                                    NovelCard(
                                        novel = novel,
                                        onClick = { onNovelClick(novel.id) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.sectionSpacing))
                        } else {
                            Text(
                                text = "No recommendations yet. Try another topic.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Spacer below section
                        Spacer(modifier = Modifier.height(Spacing.sm))
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Ensure first load uses personalized AI (no topic) when screen opens
    LaunchedEffect(Unit) {
        // no-op; the view model already loads data on init
        // Topic-based flow is activated when user taps "Ask AI"
    }
}