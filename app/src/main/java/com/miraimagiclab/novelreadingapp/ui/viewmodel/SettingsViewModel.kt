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

    val isDarkMode: StateFlow<Boolean> = settingsRepository.getDarkModeFlow()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun toggleDarkMode() {
        viewModelScope.launch {
            val currentMode = isDarkMode.value
            settingsRepository.setDarkMode(!currentMode)
        }
    }

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(isDark)
        }
    }
}
