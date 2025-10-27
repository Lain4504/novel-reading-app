package com.miraimagiclab.novelreadingapp.ui.theme

import androidx.compose.ui.graphics.Color

data class ReadingTheme(
    val name: String,
    val backgroundColor: Color,
    val textColor: Color,
    val surfaceColor: Color,
    val onSurfaceColor: Color
)

object ReadingThemes {
    val Light = ReadingTheme(
        name = "light",
        backgroundColor = Color(0xFFFFFFFF),
        textColor = Color(0xFF1A1A1A),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF1A1A1A)
    )

    val Dark = ReadingTheme(
        name = "dark",
        backgroundColor = Color(0xFF121212),
        textColor = Color(0xFFE1E1E1),
        surfaceColor = Color(0xFF1E1E1E),
        onSurfaceColor = Color(0xFFE1E1E1)
    )

    val Sand = ReadingTheme(
        name = "sand",
        backgroundColor = Color(0xFFF5F1E8),
        textColor = Color(0xFF5D4E37),
        surfaceColor = Color(0xFFF9F6F0),
        onSurfaceColor = Color(0xFF5D4E37)
    )

    val Gray = ReadingTheme(
        name = "gray",
        backgroundColor = Color(0xFFF5F5F5),
        textColor = Color(0xFF424242),
        surfaceColor = Color(0xFFFAFAFA),
        onSurfaceColor = Color(0xFF424242)
    )

    fun getThemeByName(name: String): ReadingTheme {
        return when (name) {
            "dark" -> Dark
            "sand" -> Sand
            "gray" -> Gray
            else -> Light
        }
    }

    val allThemes = listOf(Light, Dark, Sand, Gray)
}
