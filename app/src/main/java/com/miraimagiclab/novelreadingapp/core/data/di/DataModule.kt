package com.miraimagiclab.novelreadingapp.core.data.di

import com.miraimagiclab.novelreadingapp.core.data.api.ApiService
import com.miraimagiclab.novelreadingapp.core.data.repository.BookRepository
import com.miraimagiclab.novelreadingapp.core.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideBookRepository(apiService: ApiService): BookRepository {
        return BookRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(apiService: ApiService): UserRepository {
        return UserRepository(apiService)
    }
}
