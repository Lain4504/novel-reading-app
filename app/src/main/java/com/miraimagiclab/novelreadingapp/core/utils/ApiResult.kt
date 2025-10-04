package com.miraimagiclab.novelreadingapp.core.utils

/**
 * A generic class that holds a value or an error.
 * Used for handling API responses in a type-safe way.
 */
sealed class ApiResult<out T> {
    object Loading : ApiResult<Nothing>()
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}
