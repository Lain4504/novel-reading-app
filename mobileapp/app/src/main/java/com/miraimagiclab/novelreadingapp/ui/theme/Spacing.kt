package com.miraimagiclab.novelreadingapp.ui.theme

import androidx.compose.ui.unit.dp

// Consistent modular spacing system for rhythm and hierarchy
object Spacing {
    // Base spacing unit for consistent rhythm
    val xs = 4.dp      // Extra small spacing for tight elements
    val sm = 8.dp      // Small spacing for closely related elements
    val md = 12.dp     // Medium spacing for standard separation
    val lg = 16.dp     // Large spacing for section separation
    val xl = 24.dp     // Extra large spacing for major sections
    val xxl = 32.dp    // Extra extra large spacing for page-level separation
    val xxxl = 48.dp   // Maximum spacing for dramatic separation
    
    // Component-specific spacing
    val cardPadding = lg
    val cardSpacing = md
    val sectionSpacing = xl
    val contentPadding = lg
    val buttonPadding = md
    val iconSpacing = sm
    val textSpacing = xs
}
