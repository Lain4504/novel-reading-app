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
import com.miraimagiclab.novelreadingapp.service.FcmService
import com.miraimagiclab.novelreadingapp.util.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var sessionManager: SessionManager
    
    @Inject
    lateinit var tokenRefreshRepository: TokenRefreshRepository
    
    @Inject
    lateinit var fcmService: FcmService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Ensure notification channels are created
        NotificationHelper.ensureChannels(this)
        
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
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val isSystemInDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val notifEnabled by settingsViewModel.isNovelUpdateNotificationsEnabled.collectAsState()
            val authState by sessionManager.authState.collectAsState()
            
            // Determine dark theme based on theme mode
            val darkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme // "system"
            }
            
            // Send FCM token to server when user is logged in and notifications are enabled
            LaunchedEffect(authState.isLoggedIn, notifEnabled) {
                android.util.Log.d("MainActivity", "=== LaunchedEffect: FCM Token Registration ===")
                android.util.Log.d("MainActivity", "isLoggedIn: ${authState.isLoggedIn}")
                android.util.Log.d("MainActivity", "notifEnabled: $notifEnabled")
                
                if (authState.isLoggedIn && notifEnabled) {
                    android.util.Log.d("MainActivity", "User is logged in and notifications enabled. Getting FCM token...")
                    // Get FCM token and send to server
                    val token = fcmService.getFcmTokenAsync()
                    token?.let {
                        android.util.Log.d("MainActivity", "FCM token obtained. Sending to server...")
                        fcmService.sendTokenToServer(it)
                    } ?: run {
                        android.util.Log.w("MainActivity", "⚠️ FCM token is null. Cannot send to server.")
                    }
                } else {
                    android.util.Log.d("MainActivity", "Skipping FCM token registration: isLoggedIn=${authState.isLoggedIn}, notifEnabled=$notifEnabled")
                }
            }
            
            NovelReadingAppTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    
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