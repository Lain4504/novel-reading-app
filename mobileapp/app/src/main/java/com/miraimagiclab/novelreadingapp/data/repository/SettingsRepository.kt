package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.local.prefs.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    fun getThemeModeFlow(): Flow<String> = settingsDataStore.themeMode
    
    fun getDarkModeFlow(): Flow<Boolean> = settingsDataStore.isDarkMode

    fun getHasSeenOnboardingFlow(): Flow<Boolean> = settingsDataStore.hasSeenOnboarding

    fun getNovelUpdateNotificationsEnabledFlow(): Flow<Boolean> = settingsDataStore.enableNovelUpdateNotifications

    fun setThemeMode(mode: String) {
        settingsDataStore.setThemeMode(mode)
    }
    
    fun setDarkMode(isDark: Boolean) {
        settingsDataStore.setDarkMode(isDark)
    }

    fun setHasSeenOnboarding(hasSeen: Boolean) {
        settingsDataStore.setHasSeenOnboarding(hasSeen)
    }

    fun setNovelUpdateNotificationsEnabled(enabled: Boolean) {
        settingsDataStore.setEnableNovelUpdateNotifications(enabled)
    }

    fun clearSettings() {
        settingsDataStore.clear()
    }
}
