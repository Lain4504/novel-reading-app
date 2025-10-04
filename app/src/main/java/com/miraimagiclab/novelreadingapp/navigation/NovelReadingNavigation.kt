package com.miraimagiclab.novelreadingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.miraimagiclab.novelreadingapp.feature.home.presentation.ui.screens.HomeScreen
import com.miraimagiclab.novelreadingapp.feature.book.presentation.ui.screens.BookDetailsScreen
import com.miraimagiclab.novelreadingapp.feature.book.presentation.ui.screens.BookListScreen
import com.miraimagiclab.novelreadingapp.feature.content.presentation.explore.ExploreScreen
import com.miraimagiclab.novelreadingapp.feature.user.presentation.profile.ProfileScreen

@Composable
fun NovelReadingNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetails.createRoute(bookId))
                }
            )
        }
        
        composable(Screen.Explore.route) {
            ExploreScreen()
        }
        
        composable(Screen.BookList.route) {
            BookListScreen()
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen()
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
