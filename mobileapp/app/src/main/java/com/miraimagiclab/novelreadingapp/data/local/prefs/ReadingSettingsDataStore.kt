package com.miraimagiclab.novelreadingapp.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.readingSettingsDataStore by preferencesDataStore(name = "reading_settings")

class ReadingSettingsDataStore(private val context: Context) {

    object Keys {
        val FONT_FAMILY = stringPreferencesKey("font_family")
        val FONT_SIZE = floatPreferencesKey("font_size")
        val LINE_SPACING = floatPreferencesKey("line_spacing")
        val READING_THEME = stringPreferencesKey("reading_theme")
    }

    val fontFamily: Flow<String> = context.readingSettingsDataStore.data.map { preferences ->
        preferences[Keys.FONT_FAMILY] ?: "system"
    }

    val fontSize: Flow<Float> = context.readingSettingsDataStore.data.map { preferences ->
        preferences[Keys.FONT_SIZE] ?: 16f
    }

    val lineSpacing: Flow<Float> = context.readingSettingsDataStore.data.map { preferences ->
        preferences[Keys.LINE_SPACING] ?: 1.5f
    }

    val readingTheme: Flow<String> = context.readingSettingsDataStore.data.map { preferences ->
        preferences[Keys.READING_THEME] ?: "light"
    }

    suspend fun setFontFamily(fontFamily: String) {
        context.readingSettingsDataStore.edit { preferences ->
            preferences[Keys.FONT_FAMILY] = fontFamily
        }
    }

    suspend fun setFontSize(fontSize: Float) {
        context.readingSettingsDataStore.edit { preferences ->
            preferences[Keys.FONT_SIZE] = fontSize
        }
    }

    suspend fun setLineSpacing(lineSpacing: Float) {
        context.readingSettingsDataStore.edit { preferences ->
            preferences[Keys.LINE_SPACING] = lineSpacing
        }
    }

    suspend fun setReadingTheme(theme: String) {
        context.readingSettingsDataStore.edit { preferences ->
            preferences[Keys.READING_THEME] = theme
        }
    }

    suspend fun clear() {
        context.readingSettingsDataStore.edit { it.clear() }
    }
}
