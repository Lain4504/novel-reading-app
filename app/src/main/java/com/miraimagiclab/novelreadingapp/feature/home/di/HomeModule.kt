package com.miraimagiclab.novelreadingapp.feature.home.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Home feature module - simplified
 * Uses shared repositories from core/data
 */
@Module
@InstallIn(SingletonComponent::class)
object HomeModule {
    // No specific bindings needed - uses shared repositories
}
