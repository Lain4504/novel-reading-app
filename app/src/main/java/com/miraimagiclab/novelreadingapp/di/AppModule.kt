package com.miraimagiclab.novelreadingapp.di

import android.content.Context
import com.miraimagiclab.novelreadingapp.data.local.prefs.AuthDataStore
import com.miraimagiclab.novelreadingapp.data.local.prefs.ReadingSettingsDataStore
import com.miraimagiclab.novelreadingapp.data.local.prefs.SettingsDataStore
import com.miraimagiclab.novelreadingapp.data.repository.NovelRepositoryImpl
import com.miraimagiclab.novelreadingapp.data.repository.NovelDetailRepositoryImpl
import com.miraimagiclab.novelreadingapp.data.repository.ChapterRepositoryImpl
import com.miraimagiclab.novelreadingapp.data.repository.UserNovelInteractionRepositoryImpl
import com.miraimagiclab.novelreadingapp.data.repository.ReadingSettingsRepository
import com.miraimagiclab.novelreadingapp.data.repository.SettingsRepository
import com.miraimagiclab.novelreadingapp.data.repository.ReadingProgressRepository
import com.miraimagiclab.novelreadingapp.domain.repository.NovelRepository
import com.miraimagiclab.novelreadingapp.domain.repository.NovelDetailRepository
import com.miraimagiclab.novelreadingapp.domain.repository.ChapterRepository
import com.miraimagiclab.novelreadingapp.domain.repository.UserNovelInteractionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNovelRepository(
        novelRepositoryImpl: NovelRepositoryImpl
    ): NovelRepository

    @Binds
    @Singleton
    abstract fun bindNovelDetailRepository(
        novelDetailRepositoryImpl: NovelDetailRepositoryImpl
    ): NovelDetailRepository

    @Binds
    @Singleton
    abstract fun bindChapterRepository(
        chapterRepositoryImpl: ChapterRepositoryImpl
    ): ChapterRepository

    @Binds
    @Singleton
    abstract fun bindUserNovelInteractionRepository(
        userNovelInteractionRepositoryImpl: UserNovelInteractionRepositoryImpl
    ): UserNovelInteractionRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthDataStore(@ApplicationContext context: Context): AuthDataStore {
            return AuthDataStore(context)
        }

        @Provides
        @Singleton
        fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
            return SettingsDataStore(context)
        }

        @Provides
        @Singleton
        fun provideReadingSettingsDataStore(@ApplicationContext context: Context): ReadingSettingsDataStore {
            return ReadingSettingsDataStore(context)
        }

        @Provides
        @Singleton
        fun provideReadingProgressRepository(
            userNovelInteractionApiService: com.miraimagiclab.novelreadingapp.data.remote.api.UserNovelInteractionApiService
        ): ReadingProgressRepository {
            return ReadingProgressRepository(userNovelInteractionApiService)
        }


    }
}
