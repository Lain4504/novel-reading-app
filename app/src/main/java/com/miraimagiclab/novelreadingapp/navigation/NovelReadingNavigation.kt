package com.miraimagiclab.novelreadingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.miraimagiclab.novelreadingapp.ui.screens.*

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
