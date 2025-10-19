package com.miraimagiclab.novelreadingapp.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

class SettingsDataStore(private val context: Context) {

    object Keys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    val isDarkMode: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[Keys.IS_DARK_MODE] ?: false // Default to light mode
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.IS_DARK_MODE] = isDark
        }
    }

    suspend fun clear() {
        context.settingsDataStore.edit { it.clear() }
    }
}
