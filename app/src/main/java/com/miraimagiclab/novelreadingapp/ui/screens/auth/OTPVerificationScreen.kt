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
    onResendCode: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var canResend by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(60) }

    // Timer for resend button
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        canResend = true
    }

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
                    isLoading = true
                    onSubmit(otpCode)
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
