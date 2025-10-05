package com.miraimagiclab.novelreadingapp.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Home : Screen("home")
    object Explore : Screen("explore")
    object BookList : Screen("book_list")
    object Profile : Screen("profile")
    object InProgress : Screen("in_progress")
    object CompletedBook : Screen("completed_book")
    
    object BookDetails : Screen(
        route = "book_details/{bookId}",
        arguments = listOf(
            navArgument("bookId") {
                type = NavType.StringType
            }
        )
    ) {
        fun createRoute(bookId: String) = "book_details/$bookId"
    }
    
    object Reading : Screen(
        route = "reading/{bookId}/{chapterId}",
        arguments = listOf(
            navArgument("bookId") {
                type = NavType.StringType
            },
            navArgument("chapterId") {
                type = NavType.StringType
            }
        )
    ) {
        fun createRoute(bookId: String, chapterId: String) = "reading/$bookId/$chapterId"
    }
}
