package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.data.remote.api.NotificationApiService
import com.miraimagiclab.novelreadingapp.data.remote.dto.NotificationDto
import com.miraimagiclab.novelreadingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationApiService: NotificationApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _notificationsState = MutableStateFlow<UiState<List<NotificationDto>>>(UiState.Idle)
    val notificationsState: StateFlow<UiState<List<NotificationDto>>> = _notificationsState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentPage = 0
    private val pageSize = 20
    private var hasMorePages = true
    private val allNotifications = mutableListOf<NotificationDto>()

    fun loadNotifications(refresh: Boolean = false) {
        viewModelScope.launch {
            try {
                val authState = sessionManager.authState.value
                if (!authState.isLoggedIn || authState.userId == null) {
                    _notificationsState.value = UiState.Error("User not logged in")
                    return@launch
                }

                if (refresh) {
                    _isRefreshing.value = true
                    currentPage = 0
                    hasMorePages = true
                    allNotifications.clear()
                } else {
                    if (!hasMorePages) return@launch
                    _notificationsState.value = UiState.Loading
                }

                val response = notificationApiService.getNotificationsByUserId(
                    userId = authState.userId,
                    page = currentPage,
                    size = pageSize
                )

                if (response.success && response.data != null) {
                    val newNotifications = response.data.content
                    if (refresh) {
                        allNotifications.clear()
                    }
                    allNotifications.addAll(newNotifications)
                    hasMorePages = !response.data.last
                    currentPage++

                    _notificationsState.value = UiState.Success(allNotifications.toList())
                } else {
                    _notificationsState.value = UiState.Error("Failed to load notifications: ${response.message}")
                }
            } catch (e: Exception) {
                _notificationsState.value = UiState.Error("Failed to load notifications: ${e.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val response = notificationApiService.markAsRead(notificationId)
                if (response.success && response.data != null) {
                    // Update the notification in the list
                    val currentState = _notificationsState.value
                    if (currentState is UiState.Success) {
                        val updatedNotifications = currentState.data.map { notification ->
                            if (notification.id == notificationId) {
                                response.data!!
                            } else {
                                notification
                            }
                        }
                        allNotifications.clear()
                        allNotifications.addAll(updatedNotifications)
                        _notificationsState.value = UiState.Success(updatedNotifications)
                    }
                }
            } catch (e: Exception) {
                // Silently fail - notification might already be marked as read
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                val response = notificationApiService.deleteNotification(notificationId)
                if (response.success) {
                    // Remove the notification from the list
                    val currentState = _notificationsState.value
                    if (currentState is UiState.Success) {
                        val updatedNotifications = currentState.data.filter { it.id != notificationId }
                        allNotifications.clear()
                        allNotifications.addAll(updatedNotifications)
                        _notificationsState.value = UiState.Success(updatedNotifications)
                    }
                }
            } catch (e: Exception) {
                // Handle error - could show a toast
            }
        }
    }

    fun refreshNotifications() {
        loadNotifications(refresh = true)
    }
}

