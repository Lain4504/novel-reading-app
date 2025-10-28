package com.miraimagiclab.novelreadingapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Done
import androidx.compose.ui.res.painterResource
import com.miraimagiclab.novelreadingapp.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.miraimagiclab.novelreadingapp.ui.theme.ReadingThemes
import com.miraimagiclab.novelreadingapp.ui.theme.getFontFamilyByName
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ReadingSettingsViewModel
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ReadingSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingSettingsDialog(
    viewModel: ReadingSettingsViewModel,
    onDismiss: () -> Unit
) {
    val fontFamily by viewModel.fontFamily.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val lineSpacing by viewModel.lineSpacing.collectAsState()
    val readingTheme by viewModel.readingTheme.collectAsState()
    
    // Quick access presets
    val quickPresets = listOf(
        "Small" to ReadingSettings("system", 14f, 1.2f, readingTheme),
        "Medium" to ReadingSettings("system", 16f, 1.5f, readingTheme),
        "Large" to ReadingSettings("system", 18f, 1.8f, readingTheme),
        "Comfort" to ReadingSettings("literata", 17f, 1.6f, readingTheme)
    )
    
    // Preview text
    val previewText = "The quick brown fox jumps over the lazy dog. This is a preview of how your text will look with the current settings."

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reading Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        IconButton(
                            onClick = {
                                // Reset to defaults
                                viewModel.setFontFamily("system")
                                viewModel.setFontSize(16f)
                                viewModel.setLineSpacing(1.5f)
                                viewModel.setReadingTheme("light")
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.restart_alt_24px),
                                contentDescription = "Reset to defaults"
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Done"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Quick Access Presets
                    Text(
                        text = "Quick Presets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickPresets.forEach { (name, preset) ->
                            FilterChip(
                                onClick = {
                                    viewModel.setFontFamily(preset.fontFamily)
                                    viewModel.setFontSize(preset.fontSize)
                                    viewModel.setLineSpacing(preset.lineSpacing)
                                },
                                label = { Text(name) },
                                selected = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Preview Section
                    Text(
                        text = "Preview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ReadingThemes.getThemeByName(readingTheme).backgroundColor)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = previewText,
                            color = ReadingThemes.getThemeByName(readingTheme).textColor,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = getFontFamilyByName(fontFamily),
                                lineHeight = (fontSize * lineSpacing).sp,
                                fontSize = fontSize.sp
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    // Font Family Section
                    Text(
                        text = "Font Family",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val fontOptions = listOf(
                        "system" to "System Default",
                        "palatino" to "Palatino",
                        "nunito" to "Nunito",
                        "literata" to "Literata",
                        "andika" to "Andika"
                    )

                    fontOptions.forEach { (value, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setFontFamily(value) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = fontFamily == value,
                                onClick = { viewModel.setFontFamily(value) }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Font Size Section
                    Text(
                        text = "Font Size",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "${fontSize.toInt()}sp",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Slider(
                        value = fontSize,
                        onValueChange = { viewModel.setFontSize(it) },
                        valueRange = 12f..24f,
                        steps = 11, // 12, 13, 14, ..., 24
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Line Spacing Section
                    Text(
                        text = "Line Spacing",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "${String.format("%.1f", lineSpacing)}x",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Slider(
                        value = lineSpacing,
                        onValueChange = { viewModel.setLineSpacing(it) },
                        valueRange = 1.0f..2.5f,
                        steps = 14, // 1.0, 1.1, 1.2, ..., 2.5
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Reading Theme Section
                    Text(
                        text = "Reading Theme",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val themes = ReadingThemes.allThemes
                    themes.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setReadingTheme(theme.name) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = readingTheme == theme.name,
                                onClick = { viewModel.setReadingTheme(theme.name) }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            // Theme preview
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(theme.backgroundColor)
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(theme.textColor)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = theme.name.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
