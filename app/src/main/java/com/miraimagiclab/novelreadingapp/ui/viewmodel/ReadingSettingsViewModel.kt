package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.repository.ReadingSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReadingSettings(
    val fontFamily: String,
    val fontSize: Float,
    val lineSpacing: Float,
    val readingTheme: String
)

@HiltViewModel
class ReadingSettingsViewModel @Inject constructor(
    private val readingSettingsRepository: ReadingSettingsRepository
) : ViewModel() {

    val fontFamily: StateFlow<String> = readingSettingsRepository.getFontFamilyFlow()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = "system"
        )

    val fontSize: StateFlow<Float> = readingSettingsRepository.getFontSizeFlow()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = 16f
        )

    val lineSpacing: StateFlow<Float> = readingSettingsRepository.getLineSpacingFlow()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = 1.5f
        )

    val readingTheme: StateFlow<String> = readingSettingsRepository.getReadingThemeFlow()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = "light"
        )

    fun setFontFamily(fontFamily: String) {
        viewModelScope.launch {
            readingSettingsRepository.setFontFamily(fontFamily)
        }
    }

    fun setFontSize(fontSize: Float) {
        viewModelScope.launch {
            readingSettingsRepository.setFontSize(fontSize)
        }
    }

    fun setLineSpacing(lineSpacing: Float) {
        viewModelScope.launch {
            readingSettingsRepository.setLineSpacing(lineSpacing)
        }
    }

    fun setReadingTheme(theme: String) {
        viewModelScope.launch {
            readingSettingsRepository.setReadingTheme(theme)
        }
    }
}
