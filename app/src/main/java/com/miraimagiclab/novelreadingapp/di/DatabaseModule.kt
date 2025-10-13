package com.miraimagiclab.novelreadingapp.di

import android.content.Context
import androidx.room.Room
import com.miraimagiclab.novelreadingapp.data.local.dao.NovelDao
import com.miraimagiclab.novelreadingapp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "novel_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideNovelDao(database: AppDatabase): NovelDao {
        return database.novelDao()
    }
}
