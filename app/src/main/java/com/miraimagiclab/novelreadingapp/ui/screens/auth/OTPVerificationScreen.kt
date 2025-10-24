package com.miraimagiclab.novelreadingapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPVerificationScreen(
    email: String,
    type: String, // "password-reset" or "account-verification"
    onBackClick: () -> Unit,
    onSubmit: (code: String) -> Unit,
    onResendCode: () -> Unit,
    onSuccess: () -> Unit = {},
    viewModel: com.miraimagiclab.novelreadingapp.ui.viewmodel.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var canResend by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(60) }
    var showError by remember { mutableStateOf<String?>(null) }

    val verifyAccountState by viewModel.verifyAccountState.collectAsState()

    // Timer for resend button
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        canResend = true
    }
    
    // Handle verification state changes
    LaunchedEffect(verifyAccountState) {
        when (verifyAccountState) {
            is com.miraimagiclab.novelreadingapp.util.UiState.Success<*> -> {
                isLoading = false
                showError = "Account verified successfully!"
                delay(2000)
                showError = null
                // Navigate to login after successful verification
                onSuccess()
            }
            is com.miraimagiclab.novelreadingapp.util.UiState.Error -> {
                isLoading = false
                showError = (verifyAccountState as com.miraimagiclab.novelreadingapp.util.UiState.Error).message
                delay(3000)
                showError = null
                viewModel.resetVerifyAccountState()
            }
            is com.miraimagiclab.novelreadingapp.util.UiState.Loading -> {
                isLoading = true
            }
            else -> {}
        }
    }

    // Debug: Show current state
    println("DEBUG: OTPVerificationScreen - verifyAccountState: $verifyAccountState")
    println("DEBUG: OTPVerificationScreen - showError: $showError")
    println("DEBUG: OTPVerificationScreen - isLoading: $isLoading")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (type == "password-reset") "Reset Password" 
                        else "Verify Account"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter the verification code sent to $email",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = Spacing.xl)
            )
            // Error/Success message
            showError?.let {
                val isSuccess = it.contains("successfully")
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = if (isSuccess) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer
                               else MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            TextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) otpCode = it },
                label = { Text("Verification Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            Button(
                onClick = {
                    if (type == "account-verification") {
                        viewModel.verifyAccountOtp(email, otpCode)
                    } else {
                        isLoading = true
                        onSubmit(otpCode)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = otpCode.length == 6 && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Verify")
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            TextButton(
                onClick = {
                    canResend = false
                    onResendCode()
                },
                enabled = canResend
            ) {
                Text(if (canResend) "Resend Code" else "Resend Code in ${timeLeft}s")
            }
        }
    }
}
