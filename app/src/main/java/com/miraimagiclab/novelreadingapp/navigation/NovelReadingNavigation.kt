package com.miraimagiclab.novelreadingapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.miraimagiclab.novelreadingapp.ui.screens.BookDetailsScreen
import com.miraimagiclab.novelreadingapp.ui.screens.HomeScreen

@Composable
fun NovelReadingNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetails.createRoute(bookId))
                }
            )
        }
        
        composable(
            route = Screen.BookDetails.route,
            arguments = Screen.BookDetails.arguments
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailsScreen(
                bookId = bookId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
