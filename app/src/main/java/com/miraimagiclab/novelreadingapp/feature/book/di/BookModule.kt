package com.miraimagiclab.novelreadingapp.feature.book.di

import com.miraimagiclab.novelreadingapp.feature.book.data.repository.BookRepositoryImpl
import com.miraimagiclab.novelreadingapp.feature.book.domain.repository.BookRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BookModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository
}
