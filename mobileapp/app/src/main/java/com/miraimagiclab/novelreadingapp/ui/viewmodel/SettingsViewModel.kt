package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<String> = settingsRepository.getThemeModeFlow()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = "system"
        )

    val isDarkMode: StateFlow<Boolean> = settingsRepository.getDarkModeFlow()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val hasSeenOnboarding: StateFlow<Boolean> = settingsRepository.getHasSeenOnboardingFlow()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isNovelUpdateNotificationsEnabled: StateFlow<Boolean> = settingsRepository.getNovelUpdateNotificationsEnabledFlow()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }
    
    fun toggleDarkMode() {
        viewModelScope.launch {
            val currentMode = themeMode.value
            val newMode = when (currentMode) {
                "light" -> "dark"
                "dark" -> "light"
                else -> "dark" // system -> dark
            }
            settingsRepository.setThemeMode(newMode)
        }
    }

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(isDark)
        }
    }

    fun setHasSeenOnboarding(hasSeen: Boolean) {
        settingsRepository.setHasSeenOnboarding(hasSeen)
    }

    fun setNovelUpdateNotificationsEnabled(enabled: Boolean) {
        settingsRepository.setNovelUpdateNotificationsEnabled(enabled)
    }
}
