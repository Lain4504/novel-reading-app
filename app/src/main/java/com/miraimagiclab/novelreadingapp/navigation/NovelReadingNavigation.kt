package com.miraimagiclab.novelreadingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.miraimagiclab.novelreadingapp.ui.screens.*
import com.miraimagiclab.novelreadingapp.ui.screens.auth.*
import com.miraimagiclab.novelreadingapp.ui.screens.author.*
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Composable
fun NovelReadingNavigation(
    navController: NavHostController,
    sessionManager: SessionManager,
    modifier: Modifier = Modifier
) {
    val authState by sessionManager.authState.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNovelClick = { novelId ->
                    navController.navigate(Screen.BookDetails.createRoute(novelId))
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                sessionManager = sessionManager
            )
        }
        
        composable(Screen.Explore.route) {
            ExploreScreen(
                onBookClick = { novelId ->
                    navController.navigate(Screen.BookDetails.createRoute(novelId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.BookList.route) {
            BookListScreen(
                onBookClick = { novelId ->
                    navController.navigate(Screen.BookDetails.createRoute(novelId))
                },
                onNavigateInProgress = {
                    navController.navigate(Screen.InProgress.route)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                sessionManager = sessionManager
            )
        }
        
        composable(Screen.InProgress.route) {
            InProgressScreen(
                navController = navController,
                onBookClick = { novelId ->
                    navController.navigate(Screen.BookDetails.createRoute(novelId))
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        // Clear the back stack to prevent going back to profile
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                onBecomeAuthorClick = {
                    navController.navigate(Screen.BecomeAuthor.route)
                },
                onMyNovelsClick = {
                    navController.navigate(Screen.AuthorDashboard.route)
                },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Auth routes
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Screen.RegisterWithEmail.route) {
            RegisterWithEmailScreen(navController = navController)
        }

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
                    } else if (type == "account-verification") {
                        // Account verification is handled by the ViewModel, navigation will be triggered on success
                    }
                },
                onResendCode = {
                    // Implement resend logic
                    },
                onSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.OTPVerification.route) { inclusive = true }
                    }
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
                    }
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
            
            ReadingScreen(
                novelId = bookId,
                chapterId = chapterId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Author screens
        composable(Screen.BecomeAuthor.route) {
            BecomeAuthorScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AuthorDashboard.route) {
            AuthorDashboardScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCreateNovelClick = {
                    navController.navigate(Screen.CreateNovel.route)
                },
                onNovelClick = { novelId ->
                    navController.navigate(Screen.NovelManage.createRoute(novelId))
                }
            )
        }

        composable(Screen.CreateNovel.route) {
            CreateNovelScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccess = { novelId ->
                    navController.navigate(Screen.NovelManage.createRoute(novelId)) {
                        popUpTo(Screen.AuthorDashboard.route)
                    }
                }
            )
        }

        composable(
            route = Screen.EditNovel.route,
            arguments = Screen.EditNovel.arguments
        ) { backStackEntry ->
            val novelId = backStackEntry.arguments?.getString("novelId") ?: ""
            
            EditNovelScreen(
                novelId = novelId,
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.NovelManage.route,
            arguments = Screen.NovelManage.arguments
        ) { backStackEntry ->
            val novelId = backStackEntry.arguments?.getString("novelId") ?: ""
            
            NovelManageScreen(
                novelId = novelId,
                onBackClick = {
                    navController.popBackStack()
                },
                onCreateChapterClick = {
                    navController.navigate(Screen.CreateChapter.createRoute(novelId))
                },
                onEditChapterClick = { chapterId ->
                    navController.navigate(Screen.EditChapter.createRoute(novelId, chapterId))
                },
                onEditNovelClick = {
                    navController.navigate(Screen.EditNovel.createRoute(novelId))
                }
            )
        }

        composable(
            route = Screen.CreateChapter.route,
            arguments = Screen.CreateChapter.arguments
        ) { backStackEntry ->
            val novelId = backStackEntry.arguments?.getString("novelId") ?: ""
            
            CreateChapterScreen(
                novelId = novelId,
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditChapter.route,
            arguments = Screen.EditChapter.arguments
        ) { backStackEntry ->
            val novelId = backStackEntry.arguments?.getString("novelId") ?: ""
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            
            EditChapterScreen(
                novelId = novelId,
                chapterId = chapterId,
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}
