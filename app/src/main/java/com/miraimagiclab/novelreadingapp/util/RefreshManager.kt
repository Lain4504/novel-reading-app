package com.miraimagiclab.novelreadingapp.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager to handle global refresh events across the app
 * Used to trigger refresh after create/edit/delete operations
 */
@Singleton
class RefreshManager @Inject constructor() {
    private val _refreshEvent = MutableSharedFlow<RefreshType>(replay = 0)
    val refreshEvent: SharedFlow<RefreshType> = _refreshEvent.asSharedFlow()
    
    suspend fun triggerRefresh(type: RefreshType = RefreshType.ALL) {
        _refreshEvent.emit(type)
    }
}

enum class RefreshType {
    HOME,
    EXPLORE,
    BOOKLIST,
    ALL
}

