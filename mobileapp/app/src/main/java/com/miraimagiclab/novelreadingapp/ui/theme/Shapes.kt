package com.miraimagiclab.novelreadingapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Modern shape system with gentle rounded forms for friendly, approachable feel
val AppShapes = Shapes(
    // Small shapes for compact elements
    small = RoundedCornerShape(8.dp),
    
    // Medium shapes for standard components
    medium = RoundedCornerShape(12.dp),
    
    // Large shapes for prominent elements
    large = RoundedCornerShape(16.dp),
    
    // Extra large shapes for special components
    extraLarge = RoundedCornerShape(24.dp)
)

// Custom shapes for specific components
object CustomShapes {
    val cardShape = RoundedCornerShape(12.dp)
    val buttonShape = RoundedCornerShape(8.dp)
    val bannerShape = RoundedCornerShape(16.dp)
    val bookCoverShape = RoundedCornerShape(8.dp)
    val rankingItemShape = RoundedCornerShape(12.dp)
    val chipShape = RoundedCornerShape(20.dp)
    val dialogShape = RoundedCornerShape(16.dp)
    val bottomSheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
}
