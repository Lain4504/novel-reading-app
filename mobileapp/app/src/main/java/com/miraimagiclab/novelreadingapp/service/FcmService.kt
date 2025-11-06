package com.miraimagiclab.novelreadingapp.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.miraimagiclab.novelreadingapp.data.remote.api.DeviceTokenApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.DeviceTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmService @Inject constructor(
    private val deviceTokenApiService: DeviceTokenApiService
) {
    
    private val TAG = "FcmService"
    
    fun getFcmToken(callback: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                callback(null)
                return@addOnCompleteListener
            }
            
            val token = task.result
            Log.d(TAG, "FCM Registration Token: $token")
            callback(token)
        }
    }
    
    suspend fun getFcmTokenAsync(): String? {
        Log.d(TAG, "=== getFcmTokenAsync: Getting FCM token ===")
        return try {
            Log.d(TAG, "Calling FirebaseMessaging.getInstance().token.await()...")
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "✓ FCM token retrieved successfully")
            Log.d(TAG, "Token (first 20 chars): ${token.take(20)}...")
            Log.d(TAG, "Full token length: ${token.length}")
            token
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error getting FCM token", e)
            Log.e(TAG, "Exception type: ${e.javaClass.name}")
            Log.e(TAG, "Exception message: ${e.message}")
            e.printStackTrace()
            null
        }
    }
    
    fun sendTokenToServer(token: String) {
        Log.d(TAG, "=== sendTokenToServer: Sending FCM token to server ===")
        Log.d(TAG, "Token (first 20 chars): ${token.take(20)}...")
        Log.d(TAG, "Full token length: ${token.length}")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = DeviceTokenRequest(
                    token = token,
                    deviceType = "android"
                )
                Log.d(TAG, "DeviceTokenRequest created: deviceType=${request.deviceType}")
                Log.d(TAG, "Calling deviceTokenApiService.saveDeviceToken()...")
                
                val response = deviceTokenApiService.saveDeviceToken(request)
                
                Log.d(TAG, "Response received: success=${response.success}, message=${response.message}")
                if (response.success) {
                    Log.d(TAG, "✓ FCM token saved to server successfully")
                } else {
                    Log.e(TAG, "✗ Failed to save FCM token to server: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "✗ Error sending FCM token to server", e)
                Log.e(TAG, "Exception type: ${e.javaClass.name}")
                Log.e(TAG, "Exception message: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun deleteTokenFromServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = deviceTokenApiService.deleteDeviceToken(token)
                if (response.success) {
                    Log.d(TAG, "FCM token deleted from server successfully")
                } else {
                    Log.e(TAG, "Failed to delete FCM token from server: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting FCM token from server", e)
            }
        }
    }
}

