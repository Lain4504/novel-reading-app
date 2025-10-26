package com.miraimagiclab.novelreadingapp.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Explore : Screen("explore")
    object BookList : Screen("book_list")
    object InProgress : Screen("in_progress")
    object Profile : Screen("profile")

    // Auth Screens
    object Login : Screen("login")
    object Register : Screen("register")
    object RegisterWithEmail : Screen("register_with_email")
    object ForgotPassword : Screen("forgot_password")
    object OTPVerification : Screen(
        route = "otp_verification/{email}/{type}",
        arguments = listOf(
            navArgument("email") { type = NavType.StringType },
            navArgument("type") { type = NavType.StringType }
        )
    ) {
        fun createRoute(email: String, type: String) = "otp_verification/$email/$type"
    }
    object ResetPassword : Screen(
        route = "reset_password/{email}/{code}",
        arguments = listOf(
            navArgument("email") { type = NavType.StringType },
            navArgument("code") { type = NavType.StringType }
        )
    ) {
        fun createRoute(email: String, code: String) = "reset_password/$email/$code"
    }

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

    // Author screens
    object BecomeAuthor : Screen("become_author")
    object AuthorDashboard : Screen("author_dashboard")
    object CreateNovel : Screen("create_novel")
    object EditNovel : Screen(
        route = "edit_novel/{novelId}",
        arguments = listOf(
            navArgument("novelId") { type = NavType.StringType }
        )
    ) {
        fun createRoute(novelId: String) = "edit_novel/$novelId"
    }
    object NovelManage : Screen(
        route = "novel_manage/{novelId}",
        arguments = listOf(
            navArgument("novelId") { type = NavType.StringType }
        )
    ) {
        fun createRoute(novelId: String) = "novel_manage/$novelId"
    }
    object CreateChapter : Screen(
        route = "create_chapter/{novelId}",
        arguments = listOf(
            navArgument("novelId") { type = NavType.StringType }
        )
    ) {
        fun createRoute(novelId: String) = "create_chapter/$novelId"
    }
    object EditChapter : Screen(
        route = "edit_chapter/{novelId}/{chapterId}",
        arguments = listOf(
            navArgument("novelId") { type = NavType.StringType },
            navArgument("chapterId") { type = NavType.StringType }
        )
    ) {
        fun createRoute(novelId: String, chapterId: String) = "edit_chapter/$novelId/$chapterId"
    }
}
