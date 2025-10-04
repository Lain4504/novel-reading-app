package com.miraimagiclab.novelreadingapp.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Home : Screen("home")
    
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
}
