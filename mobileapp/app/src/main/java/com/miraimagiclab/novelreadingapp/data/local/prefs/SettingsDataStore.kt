package com.miraimagiclab.novelreadingapp.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

class SettingsDataStore(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    object Keys {
        const val THEME_MODE = "theme_mode" // "light", "dark", "system"
        const val IS_DARK_MODE = "is_dark_mode" // Old key for migration
        const val HAS_SEEN_ONBOARDING = "has_seen_onboarding"
        const val ENABLE_NOVEL_UPDATE_NOTIFICATIONS = "enable_novel_update_notifications"
    }
    
    private fun getInitialThemeMode(): String {
        // Migrate from old boolean key to new theme mode if needed
        if (!sharedPreferences.contains(Keys.THEME_MODE) && sharedPreferences.contains(Keys.IS_DARK_MODE)) {
            val oldIsDark = sharedPreferences.getBoolean(Keys.IS_DARK_MODE, false)
            val migratedMode = if (oldIsDark) "dark" else "light"
            sharedPreferences.edit()
                .putString(Keys.THEME_MODE, migratedMode)
                .remove(Keys.IS_DARK_MODE) // Remove old key
                .apply()
            return migratedMode
        }
        return sharedPreferences.getString(Keys.THEME_MODE, "system") ?: "system"
    }

    private val initialThemeMode = getInitialThemeMode()
    private val _themeMode = MutableStateFlow(initialThemeMode)
    val themeMode: Flow<String> = _themeMode.asStateFlow()
    
    // For backward compatibility - calculate from theme mode
    private val _isDarkMode = MutableStateFlow(
        when (initialThemeMode) {
            "dark" -> true
            "light" -> false
            else -> false // system defaults to light for backward compatibility
        }
    )
    val isDarkMode: Flow<Boolean> = _isDarkMode.asStateFlow()

    private val _hasSeenOnboarding = MutableStateFlow(sharedPreferences.getBoolean(Keys.HAS_SEEN_ONBOARDING, false))
    val hasSeenOnboarding: Flow<Boolean> = _hasSeenOnboarding.asStateFlow()

    private val _enableNovelUpdateNotifications = MutableStateFlow(
        sharedPreferences.getBoolean(Keys.ENABLE_NOVEL_UPDATE_NOTIFICATIONS, true)
    )
    val enableNovelUpdateNotifications: Flow<Boolean> = _enableNovelUpdateNotifications.asStateFlow()

    fun setThemeMode(mode: String) {
        sharedPreferences.edit().putString(Keys.THEME_MODE, mode).apply()
        _themeMode.value = mode
        // Update isDarkMode for backward compatibility
        _isDarkMode.value = when (mode) {
            "dark" -> true
            "light" -> false
            else -> false
        }
    }
    
    // For backward compatibility
    fun setDarkMode(isDark: Boolean) {
        val mode = if (isDark) "dark" else "light"
        setThemeMode(mode)
    }

    fun setHasSeenOnboarding(hasSeen: Boolean) {
        sharedPreferences.edit().putBoolean(Keys.HAS_SEEN_ONBOARDING, hasSeen).apply()
        _hasSeenOnboarding.value = hasSeen
    }

    fun setEnableNovelUpdateNotifications(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(Keys.ENABLE_NOVEL_UPDATE_NOTIFICATIONS, enabled).apply()
        _enableNovelUpdateNotifications.value = enabled
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
        _themeMode.value = "system"
        _isDarkMode.value = false
        _hasSeenOnboarding.value = false
        _enableNovelUpdateNotifications.value = true
    }
}
