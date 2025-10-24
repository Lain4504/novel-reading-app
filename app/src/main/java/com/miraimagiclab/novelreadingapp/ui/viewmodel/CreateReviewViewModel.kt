package com.miraimagiclab.novelreadingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miraimagiclab.novelreadingapp.data.remote.api.ReviewApiService
import com.miraimagiclab.novelreadingapp.ui.components.novel.ReviewRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateReviewViewModel @Inject constructor(
    private val reviewApiService: ReviewApiService
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    fun submitReview(novelId: String, review: ReviewRequest) {
        viewModelScope.launch {
            try {
                _isSubmitting.value = true
                
                // TODO: Implement submit review API call
                // For now, just simulate success
                // val response = reviewApiService.submitReview(novelId, review)
                
                // Simulate API call delay
                kotlinx.coroutines.delay(1000)
                
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
