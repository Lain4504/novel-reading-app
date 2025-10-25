package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.miraimagiclab.novelreadingapp.navigation.Screen
import com.miraimagiclab.novelreadingapp.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Simulate splash delay
    LaunchedEffect(Unit) {
        delay(2500) // 2.5 seconds delay for better UX
        navController.navigate(Screen.Onboarding.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GreenPrimary,
                        GreenPrimary.copy(alpha = 0.9f),
                        GreenSecondary.copy(alpha = 0.8f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Enhanced book icon with shadow and animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = GreenPrimary.copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.95f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .size(80.dp)
                ) {
                    // Enhanced book icon with more detail
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            // Left page with rounded corners
                            moveTo(12f * size.width / 24, 1.5f * size.height / 24)
                            lineTo(7f * size.width / 24, 2f * size.height / 24)
                            lineTo(3f * size.width / 24, 6f * size.height / 24)
                            lineTo(3f * size.width / 24, 18f * size.height / 24)
                            lineTo(5f * size.width / 24, 20f * size.height / 24)
                            lineTo(12f * size.width / 24, 20f * size.height / 24)
                            close()

                            // Left page lines with better spacing
                            moveTo(5.5f * size.width / 24, 7f * size.height / 24)
                            lineTo(9f * size.width / 24, 7f * size.height / 24)
                            moveTo(5.5f * size.width / 24, 10f * size.height / 24)
                            lineTo(9f * size.width / 24, 10f * size.height / 24)
                            moveTo(5.5f * size.width / 24, 13f * size.height / 24)
                            lineTo(9f * size.width / 24, 13f * size.height / 24)

                            // Right page with spine
                            moveTo(13f * size.width / 24, 1.5f * size.height / 24)
                            lineTo(13f * size.width / 24, 20f * size.height / 24)
                            lineTo(19f * size.width / 24, 20f * size.height / 24)
                            lineTo(21f * size.width / 24, 18f * size.height / 24)
                            lineTo(21f * size.width / 24, 10f * size.height / 24)
                            lineTo(17f * size.width / 24, 2f * size.height / 24)
                            close()

                            // Spine shadow
                            moveTo(12.8f * size.width / 24, 2f * size.height / 24)
                            lineTo(12.8f * size.width / 24, 20f * size.height / 24)
                        },
                        color = GreenPrimary
                    )

                    // Book spine highlight
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(12.9f * size.width / 24, 2.5f * size.height / 24)
                            lineTo(12.9f * size.width / 24, 19.5f * size.height / 24)
                        },
                        color = GreenSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enhanced app title with better typography
            Text(
                text = "Novel Reading",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Text(
                text = "Your Story Awaits",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Loading indicator
            androidx.compose.material3.LinearProgressIndicator(
                color = Color.White.copy(alpha = 0.8f),
                trackColor = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .width(200.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
        }
    }
}