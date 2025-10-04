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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.miraimagiclab.novelreadingapp.navigation.NovelReadingNavigation
import com.miraimagiclab.novelreadingapp.navigation.Screen
import com.miraimagiclab.novelreadingapp.ui.components.BottomNavigationBar
import com.miraimagiclab.novelreadingapp.ui.theme.NovelReadingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NovelReadingAppTheme {
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
                            )
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
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NovelReadingNavigation(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}