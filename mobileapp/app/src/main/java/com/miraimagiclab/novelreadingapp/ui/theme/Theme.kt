package com.miraimagiclab.novelreadingapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenPrimaryVariant,
    onPrimaryContainer = Color.White,
    
    secondary = BlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = BlueSecondaryVariant,
    onSecondaryContainer = Color.White,
    
    tertiary = TealAccent,
    onTertiary = Color.White,
    tertiaryContainer = TealAccentVariant,
    onTertiaryContainer = Color.White,
    
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceDark,
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenSecondary,
    onPrimaryContainer = GreenPrimaryVariant,
    
    secondary = BlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = BlueTertiary,
    onSecondaryContainer = BlueSecondaryVariant,
    
    tertiary = TealAccent,
    onTertiary = Color.White,
    tertiaryContainer = TealAccent.copy(alpha = 0.1f),
    onTertiaryContainer = TealAccentVariant,
    
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceLight,
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed
)

@Composable
fun NovelReadingAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color for consistent reading experience
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}