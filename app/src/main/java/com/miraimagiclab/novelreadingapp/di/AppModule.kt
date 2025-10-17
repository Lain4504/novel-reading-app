package com.miraimagiclab.novelreadingapp.di

import com.miraimagiclab.novelreadingapp.data.repository.NovelRepositoryImpl
import com.miraimagiclab.novelreadingapp.data.repository.NovelDetailRepositoryImpl
import com.miraimagiclab.novelreadingapp.domain.repository.NovelRepository
import com.miraimagiclab.novelreadingapp.domain.repository.NovelDetailRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
}
