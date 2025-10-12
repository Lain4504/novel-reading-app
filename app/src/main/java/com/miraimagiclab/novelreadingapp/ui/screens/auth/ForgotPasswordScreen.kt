package com.miraimagiclab.novelreadingapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.miraimagiclab.novelreadingapp.ui.components.EmailTextField
import com.miraimagiclab.novelreadingapp.ui.components.PasswordTextField
import com.miraimagiclab.novelreadingapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    onSubmit: (email: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Back button at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.lg, bottom = Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Forgot Password",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = Spacing.sm)
            )
        }

        // Main content centered
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter your email address and we'll send you a code to reset your password.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = Spacing.xl)
            )

            EmailTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            Button(
                onClick = {
                    isLoading = true
                    onSubmit(email)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Send Reset Code")
                }
            }
        }
    }
}
