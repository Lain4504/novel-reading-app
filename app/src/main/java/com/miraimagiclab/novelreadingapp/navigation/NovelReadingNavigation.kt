package com.miraimagiclab.novelreadingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.miraimagiclab.novelreadingapp.ui.screens.ChangePasswordScreen
import com.miraimagiclab.novelreadingapp.ui.screens.*
import com.miraimagiclab.novelreadingapp.ui.screens.auth.*
import com.miraimagiclab.novelreadingapp.ui.screens.author.*
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import android.util.Log

@Composable
fun NovelReadingNavigation(
    navController: NavHostController,
    sessionManager: SessionManager,
    modifier: Modifier = Modifier
) {
    val authState by sessionManager.authState.collectAsState()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val hasSeenOnboarding by settingsViewModel.hasSeenOnboarding.collectAsState()

    var startDestination by remember { mutableStateOf<String?>(null) }

    // Determine start destination after collecting the onboarding state
    LaunchedEffect(hasSeenOnboarding) {
        startDestination = if (hasSeenOnboarding) Screen.Home.route else Screen.Onboarding.route
    }

    // Only render NavHost when startDestination is determined
    startDestination?.let { destination ->
        NavHost(
            navController = navController,
            startDestination = destination,
            modifier = modifier
        ) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNovelClick = { novelId ->
                    navController.navigate(Screen.NovelDetail.createRoute(novelId))
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
                    navController.navigate(Screen.NovelDetail.createRoute(novelId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.BookList.route) {
            BookListScreen(
                onBookClick = { novelId ->
                    navController.navigate(Screen.NovelDetail.createRoute(novelId))
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
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
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
                },
                onPersonalDataClick = {
                    // Navigate to account detail with current user's ID
                    val currentUserId = authState.userId
                    if (currentUserId != null) {
                        navController.navigate(Screen.AccountDetail.createRoute(currentUserId))
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

            val viewModel: com.miraimagiclab.novelreadingapp.ui.viewmodel.AuthViewModel = hiltViewModel()

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
                    viewModel.resendVerification(email)
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
            route = Screen.NovelDetail.route,
            arguments = Screen.NovelDetail.arguments
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            NovelDetailScreen(
                bookId = bookId,
                onBackClick = {
                    navController.popBackStack()
                },
                onChapterClick = { chapter ->
                    navController.navigate(Screen.Reading.createRoute(bookId, chapter.id))
                },
                onNavigateToComments = { novelId ->
                    navController.navigate(Screen.Comments.createRoute(novelId))
                },
                onNavigateToCreateReview = { novelId ->
                    navController.navigate(Screen.CreateReview.createRoute(novelId))
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
                 },
                 onNavigateToNovelDetail = { novelId ->
                     // Replace Reading screen with NovelDetail in back stack
                     navController.navigate(Screen.NovelDetail.createRoute(novelId)) {
                         // Replace the current Reading screen
                         launchSingleTop = true
                         popUpTo(Screen.Reading.route) { inclusive = true }
                     }
                 },
                 sessionManager = sessionManager
             )
        }

        composable(
            route = Screen.Comments.route,
            arguments = Screen.Comments.arguments
        ) { backStackEntry ->
            val novelId = backStackEntry.arguments?.getString("novelId") ?: ""
            CommentsScreen(
                novelId = novelId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.CreateReview.route,
            arguments = Screen.CreateReview.arguments
        ) { backStackEntry ->
            val novelId = backStackEntry.arguments?.getString("novelId") ?: ""
            CreateReviewScreen(
                novelId = novelId,
                onBackClick = {
                    navController.popBackStack()
                },
                onReviewSubmitted = {
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

        composable(
            route = Screen.AccountDetail.route,
            arguments = Screen.AccountDetail.arguments
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            
            AccountDetailScreen(
                userId = userId,
                onBackClick = {
                    navController.popBackStack()
                },
                onNovelClick = { novelId ->
                    navController.navigate(Screen.NovelDetail.createRoute(novelId))
                }
            )
        }
        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
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
}
