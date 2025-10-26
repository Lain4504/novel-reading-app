 package com.miraimagiclab.novelreadingapp.ui.screens
 
 import androidx.compose.foundation.background
 import androidx.compose.foundation.layout.*
 import androidx.compose.foundation.pager.HorizontalPager
 import androidx.compose.foundation.pager.rememberPagerState
 import androidx.compose.foundation.shape.CircleShape
 import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.Add
 import androidx.compose.material.icons.filled.Star
 import androidx.compose.material.icons.filled.Search
 import androidx.compose.material3.*
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.rememberCoroutineScope
 import android.util.Log
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.draw.clip
 import androidx.compose.ui.draw.shadow
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.text.font.FontWeight
 import androidx.compose.ui.text.style.TextAlign
 import androidx.compose.ui.unit.dp
 import androidx.compose.ui.unit.sp
 import androidx.hilt.navigation.compose.hiltViewModel
 import androidx.navigation.NavController
 import com.miraimagiclab.novelreadingapp.navigation.Screen
 import com.miraimagiclab.novelreadingapp.ui.theme.*
 import com.miraimagiclab.novelreadingapp.ui.viewmodel.SettingsViewModel
 import kotlinx.coroutines.launch
 
 data class OnboardingPage(
     val icon: @Composable () -> Unit,
     val title: String,
     val description: String
 )
 
 @Composable
 fun OnboardingScreen(navController: NavController) {
     val settingsViewModel: SettingsViewModel = hiltViewModel()
 
 
     val pages = listOf(
         OnboardingPage(
             icon = {
                 Box(
                     modifier = Modifier
                         .size(100.dp)
                         .shadow(
                             elevation = 15.dp,
                             shape = CircleShape,
                             ambientColor = GreenPrimary.copy(alpha = 0.3f)
                         )
                         .clip(CircleShape)
                         .background(GreenPrimary),
                     contentAlignment = Alignment.Center
                 ) {
                     androidx.compose.foundation.Canvas(
                         modifier = Modifier.size(50.dp)
                     ) {
                         val strokeWidth = 5f
                         val centerX = size.width / 2
                         val centerY = size.height / 2
                         val halfSize = 15f
 
                         drawRoundRect(
                             color = Color.White,
                             topLeft = androidx.compose.ui.geometry.Offset(
                                 centerX - halfSize,
                                 centerY - strokeWidth / 2
                             ),
                             size = androidx.compose.ui.geometry.Size(
                                 halfSize * 2,
                                 strokeWidth
                             ),
                             cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                         )
 
                         drawRoundRect(
                             color = Color.White,
                             topLeft = androidx.compose.ui.geometry.Offset(
                                 centerX - strokeWidth / 2,
                                 centerY - halfSize
                             ),
                             size = androidx.compose.ui.geometry.Size(
                                 strokeWidth,
                                 halfSize * 2
                             ),
                             cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                         )
                     }
                 }
             },
             title = "Start Your Adventure Through Stories",
             description = "Discover a world of fantasy, action, and romance in hundreds of favorite novel and comic titles."
         ),
         OnboardingPage(
             icon = {
                 Box(
                     modifier = Modifier
                         .size(100.dp)
                         .shadow(
                             elevation = 15.dp,
                             shape = CircleShape,
                             ambientColor = GreenSecondary.copy(alpha = 0.3f)
                         )
                         .clip(CircleShape)
                         .background(GreenSecondary),
                     contentAlignment = Alignment.Center
                 ) {
                     Icon(
                         Icons.Default.Star,
                         contentDescription = null,
                         modifier = Modifier.size(50.dp),
                         tint = Color.White
                     )
                 }
             },
             title = "The more you read, the more you get!",
             description = "Every page you read can pave the way to exciting rewards. Keep reading, because every word you skip means something!"
         ),
         OnboardingPage(
             icon = {
                 Box(
                     modifier = Modifier
                         .size(100.dp)
                         .shadow(
                             elevation = 15.dp,
                             shape = CircleShape,
                             ambientColor = GreenPrimary.copy(alpha = 0.3f)
                         )
                         .clip(CircleShape)
                         .background(GreenPrimary),
                     contentAlignment = Alignment.Center
                 ) {
                     Icon(
                         Icons.Default.Search,
                         contentDescription = null,
                         modifier = Modifier.size(50.dp),
                         tint = Color.White
                     )
                 }
             },
             title = "Find a Story that Fits You",
             description = "From heartwarming romances to adrenaline-pumping adventures—explore a variety of genres."
         )
     )
 
     val pagerState = rememberPagerState(pageCount = { pages.size })
     val scope = rememberCoroutineScope()
 
     Column(
         modifier = Modifier
             .fillMaxSize()
             .background(BackgroundLight)
     ) {
         HorizontalPager(
             state = pagerState,
             modifier = Modifier.weight(1f)
         ) { page ->
             Column(
                 modifier = Modifier
                     .fillMaxSize()
                     .padding(24.dp),
                 horizontalAlignment = Alignment.CenterHorizontally,
                 verticalArrangement = Arrangement.Center
             ) {
                 pages[page].icon()
 
                 Spacer(modifier = Modifier.height(48.dp))
 
                 Text(
                     text = pages[page].title,
                     fontSize = 26.sp,
                     fontWeight = FontWeight.Bold,
                     textAlign = TextAlign.Center,
                     color = OnSurfaceLight,
                     lineHeight = 32.sp
                 )
 
                 Spacer(modifier = Modifier.height(16.dp))
 
                 Text(
                     text = pages[page].description,
                     fontSize = 16.sp,
                     textAlign = TextAlign.Center,
                     color = OnSurfaceLight.copy(alpha = 0.8f),
                     lineHeight = 24.sp,
                     modifier = Modifier.padding(horizontal = 24.dp)
                 )
             }
         }
 
         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(vertical = 16.dp),
             horizontalArrangement = Arrangement.Center,
             verticalAlignment = Alignment.CenterVertically
         ) {
             repeat(pages.size) { index ->
                 val color = if (pagerState.currentPage == index)
                     GreenPrimary
                 else
                     OnSurfaceLight.copy(alpha = 0.3f)
 
                 Box(
                     modifier = Modifier
                         .size(
                             width = if (pagerState.currentPage == index) 20.dp else 10.dp,
                             height = 10.dp
                         )
                         .clip(CircleShape)
                         .background(color)
                 )
                 if (index < pages.size - 1) Spacer(modifier = Modifier.width(8.dp))
             }
         }
 
         Column(
             modifier = Modifier
                 .fillMaxWidth()
                 .background(Color.White)
                 .padding(24.dp),
             verticalArrangement = Arrangement.spacedBy(16.dp)
         ) {
             Button(
                 onClick = {
                     // Set onboarding as seen
                     settingsViewModel.setHasSeenOnboarding(true)
                     navController.navigate(Screen.Login.route)
                 },
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(56.dp),
                 colors = ButtonDefaults.buttonColors(
                     containerColor = GreenPrimary,
                     contentColor = Color.White
                 ),
                 shape = RoundedCornerShape(16.dp)
             ) {
                 Text(
                     "Sign in",
                     fontWeight = FontWeight.Bold,
                     fontSize = 16.sp
                 )
             }
 
             OutlinedButton(
                 onClick = {
                     // Set onboarding as seen
                     settingsViewModel.setHasSeenOnboarding(true)
                     navController.navigate(Screen.Home.route) {
                         popUpTo(Screen.Onboarding.route) { inclusive = true }
                     }
                 },
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(56.dp),
                 colors = ButtonDefaults.outlinedButtonColors(
                     contentColor = GreenPrimary
                 ),
                 border = androidx.compose.foundation.BorderStroke(
                     width = 1.5.dp,
                     color = GreenPrimary
                 ),
                 shape = RoundedCornerShape(16.dp)
             ) {
                 Text(
                     "Enter as guest",
                     fontWeight = FontWeight.Bold,
                     fontSize = 16.sp
                 )
             }
         }
     }
 }