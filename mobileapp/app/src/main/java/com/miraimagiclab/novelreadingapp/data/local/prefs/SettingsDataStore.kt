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
        const val IS_DARK_MODE = "is_dark_mode"
        const val HAS_SEEN_ONBOARDING = "has_seen_onboarding"
    }

    private val _isDarkMode = MutableStateFlow(sharedPreferences.getBoolean(Keys.IS_DARK_MODE, false))
    val isDarkMode: Flow<Boolean> = _isDarkMode.asStateFlow()

    private val _hasSeenOnboarding = MutableStateFlow(sharedPreferences.getBoolean(Keys.HAS_SEEN_ONBOARDING, false))
    val hasSeenOnboarding: Flow<Boolean> = _hasSeenOnboarding.asStateFlow()

    fun setDarkMode(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(Keys.IS_DARK_MODE, isDark).apply()
        _isDarkMode.value = isDark
    }

    fun setHasSeenOnboarding(hasSeen: Boolean) {
        sharedPreferences.edit().putBoolean(Keys.HAS_SEEN_ONBOARDING, hasSeen).apply()
        _hasSeenOnboarding.value = hasSeen
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
        _isDarkMode.value = false
        _hasSeenOnboarding.value = false
    }
}
