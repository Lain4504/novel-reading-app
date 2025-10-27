package com.miraimagiclab.novelreadingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.miraimagiclab.novelreadingapp.navigation.NovelReadingNavigation
import com.miraimagiclab.novelreadingapp.navigation.Screen
import com.miraimagiclab.novelreadingapp.ui.components.BottomNavigationBar
import com.miraimagiclab.novelreadingapp.ui.theme.NovelReadingAppTheme
import com.miraimagiclab.novelreadingapp.ui.viewmodel.SettingsViewModel
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.data.repository.TokenRefreshRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var sessionManager: SessionManager
    
    @Inject
    lateinit var tokenRefreshRepository: TokenRefreshRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Check and refresh token on app startup
        lifecycleScope.launch {
            val result = tokenRefreshRepository.validateAndRefreshCurrentSession(thresholdMinutes = 60)
            when (result) {
                is com.miraimagiclab.novelreadingapp.data.repository.TokenRefreshResult.RefreshTokenExpired -> {
                    // Session will be automatically cleared by the repository
                    // Navigation will handle redirecting to login
                }
                is com.miraimagiclab.novelreadingapp.data.repository.TokenRefreshResult.Error -> {
                    // Log error but don't force logout for network errors
                    android.util.Log.w("MainActivity", "Token refresh failed: ${result.message}")
                }
                else -> {
                    // Success or no refresh needed - continue normally
                }
            }
        }
        
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            
            NovelReadingAppTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val authState by sessionManager.authState.collectAsState()
                    
                    Scaffold(
                        bottomBar = {
                            // Only show bottom navigation for main screens
                            val showBottomNav = currentRoute in listOf(
                                Screen.Home.route,
                                Screen.Explore.route,
                                Screen.BookList.route,
                                Screen.Profile.route
                            ) && currentRoute != Screen.Onboarding.route && !(currentRoute?.startsWith("book_details/") == true) && !(currentRoute?.startsWith("reading/") == true) && !(currentRoute?.startsWith("novel_detail/") == true) && !(currentRoute?.startsWith("reading/") == true)
                            if (showBottomNav) {
                                BottomNavigationBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { route ->
                                        navController.navigate(route) {
                                            // Pop up to the start destination to avoid building up a large stack
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when reselecting
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    },
                                    isLoggedIn = authState.isLoggedIn
                                )
                            }
                        }
                    ) { innerPadding ->
                        NovelReadingNavigation(
                            navController = navController,
                            sessionManager = sessionManager,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}