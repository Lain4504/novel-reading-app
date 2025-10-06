package com.miraimagiclab.novelreadingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.miraimagiclab.novelreadingapp.ui.screens.*
import com.miraimagiclab.novelreadingapp.ui.screens.auth.*
import com.miraimagiclab.novelreadingapp.data.MockData
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

        composable(Screen.BookList.route) {
            // also pass callbacks so when user navigates to BookList via bottom nav it works the same
            BookListScreen(
                onNavigateInProgress = { navController.navigate(Screen.InProgress.route) },
                onNavigateCompleted = { navController.navigate(Screen.CompletedBook.route) },
                onBookClick = { bookId -> navController.navigate(Screen.BookDetails.createRoute(bookId)) },
                onBackClick = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        composable(Screen.Explore.route) {
            ExploreScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetails.createRoute(bookId))
                },
                onBackClick = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen()
        }

<<<<<<< HEAD
        // Auth routes
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSubmit = { email ->
                    navController.navigate(Screen.OTPVerification.createRoute(email, "password-reset"))
                }
            )
        }

        composable(
            route = Screen.OTPVerification.route,
            arguments = Screen.OTPVerification.arguments
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: ""
            
            OTPVerificationScreen(
                email = email,
                type = type,
                onBackClick = {
                    navController.popBackStack()
                },
                onSubmit = { code ->
                    if (type == "password-reset") {
                        navController.navigate(Screen.ResetPassword.createRoute(email, code))
                    } else {
                        // Handle account verification success
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onResendCode = {
                    // Implement resend logic
                }
            )
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = Screen.ResetPassword.arguments
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val code = backStackEntry.arguments?.getString("code") ?: ""
            
            ResetPasswordScreen(
                email = email,
                otpCode = code,
                onBackClick = {
                    navController.popBackStack()
                },
                onSubmit = { newPassword, confirmPassword ->
                    // Handle password reset success
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
=======
        composable(Screen.InProgress.route) {
            InProgressScreen(
                navController,
                onBookClick = { bookId ->
                    val bookDetail = MockData.getBookDetail(bookId)
                    val firstChapterId = bookDetail?.chapters?.firstOrNull()?.id
                    if (firstChapterId != null) {
                        navController.navigate("reading/$bookId/$firstChapterId")
>>>>>>> 5497c25ac0d7898ed6fa4c5b7911d00b53b9be3c
                    }
                }
            )
        }
<<<<<<< HEAD
=======


        composable(Screen.CompletedBook.route) {
            CompletedBookScreen(
                navController,
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetails.createRoute(bookId))
                }
            )
        }
>>>>>>> 5497c25ac0d7898ed6fa4c5b7911d00b53b9be3c
        
        composable(
            route = Screen.BookDetails.route,
            arguments = Screen.BookDetails.arguments
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailsScreen(
                bookId = bookId,
                onBackClick = {
                    navController.popBackStack()
                },
                onChapterClick = { chapter ->
                    navController.navigate(Screen.Reading.createRoute(bookId, chapter.id))
                }
            )
        }
        
        composable(
            route = Screen.Reading.route,
            arguments = Screen.Reading.arguments
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            
            // Get book detail and chapter
            val bookDetail = com.miraimagiclab.novelreadingapp.data.MockData.getBookDetail(bookId)
            val chapter = bookDetail?.chapters?.find { it.id == chapterId }
            
            if (chapter != null) {
                val chapters = bookDetail.chapters
                val currentIndex = chapters.indexOf(chapter)
                val hasPrevious = currentIndex > 0
                val hasNext = currentIndex < chapters.size - 1
                
                ReadingScreen(
                    chapterTitle = chapter.title,
                    chapterContent = chapter.content.ifEmpty { "Chapter content not available." },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onPreviousChapter = {
                        if (hasPrevious) {
                            val prevChapter = chapters[currentIndex - 1]
                            navController.navigate(Screen.Reading.createRoute(bookId, prevChapter.id)) {
                                popUpTo(Screen.Reading.createRoute(bookId, chapterId))
                            }
                        }
                    },
                    onNextChapter = {
                        if (hasNext) {
                            val nextChapter = chapters[currentIndex + 1]
                            navController.navigate(Screen.Reading.createRoute(bookId, nextChapter.id)) {
                                popUpTo(Screen.Reading.createRoute(bookId, chapterId))
                            }
                        }
                    },
                    hasPreviousChapter = hasPrevious,
                    hasNextChapter = hasNext
                )
            }
        }
    }
}
