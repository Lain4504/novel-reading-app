package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.local.prefs.ReadingSettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadingSettingsRepository @Inject constructor(
    private val readingSettingsDataStore: ReadingSettingsDataStore
) {
    fun getFontFamilyFlow(): Flow<String> = readingSettingsDataStore.fontFamily
    fun getFontSizeFlow(): Flow<Float> = readingSettingsDataStore.fontSize
    fun getLineSpacingFlow(): Flow<Float> = readingSettingsDataStore.lineSpacing
    fun getReadingThemeFlow(): Flow<String> = readingSettingsDataStore.readingTheme

    suspend fun setFontFamily(fontFamily: String) {
        readingSettingsDataStore.setFontFamily(fontFamily)
    }

    suspend fun setFontSize(fontSize: Float) {
        readingSettingsDataStore.setFontSize(fontSize)
    }

    suspend fun setLineSpacing(lineSpacing: Float) {
        readingSettingsDataStore.setLineSpacing(lineSpacing)
    }

    suspend fun setReadingTheme(theme: String) {
        readingSettingsDataStore.setReadingTheme(theme)
    }

    suspend fun clearReadingSettings() {
        readingSettingsDataStore.clear()
    }
}
