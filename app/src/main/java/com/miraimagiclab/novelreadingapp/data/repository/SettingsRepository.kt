package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.local.prefs.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    fun getDarkModeFlow(): Flow<Boolean> = settingsDataStore.isDarkMode

    fun getHasSeenOnboardingFlow(): Flow<Boolean> = settingsDataStore.hasSeenOnboarding

    fun setDarkMode(isDark: Boolean) {
        settingsDataStore.setDarkMode(isDark)
    }

    fun setHasSeenOnboarding(hasSeen: Boolean) {
        settingsDataStore.setHasSeenOnboarding(hasSeen)
    }

    fun clearSettings() {
        settingsDataStore.clear()
    }
}
