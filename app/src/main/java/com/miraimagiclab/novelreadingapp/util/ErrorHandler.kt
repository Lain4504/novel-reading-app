package com.miraimagiclab.novelreadingapp.util

import android.util.Log
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorHandler {
    
    private const val TAG = "ErrorHandler"
    
    fun handleError(throwable: Throwable): String {
        Log.e(TAG, "Error occurred", throwable)
        
        return when (throwable) {
            is ConnectException -> "Unable to connect to server. Please check your internet connection."
            is SocketTimeoutException -> "Request timed out. Please try again."
            is UnknownHostException -> "Unable to reach server. Please check your internet connection."
            is SecurityException -> "Permission denied. Please check app permissions."
            else -> "An unexpected error occurred: ${throwable.message ?: "Unknown error"}"
        }
    }
    
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}
