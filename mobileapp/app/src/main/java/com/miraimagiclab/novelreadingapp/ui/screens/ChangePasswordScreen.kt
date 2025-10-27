package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.ui.components.PasswordTextField
import com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ChangePasswordViewModel
import com.miraimagiclab.novelreadingapp.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {},
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val changePasswordState by viewModel.changePasswordState.collectAsState()
    val context = LocalContext.current

    // Handle success state
    LaunchedEffect(changePasswordState) {
        if (changePasswordState is UiState.Success) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Change Password",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = GreenPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(40.dp)
                    )
                    .padding(20.dp),
                tint = GreenPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Change Your Password",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Enter your current password and choose a new secure password",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form fields
            var currentPassword by remember { mutableStateOf("") }
            var newPassword by remember { mutableStateOf("") }
            var confirmPassword by remember { mutableStateOf("") }

            // Current Password
            PasswordTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = "Current Password",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Password
            PasswordTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "New Password",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm New Password
            PasswordTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm New Password",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            if (changePasswordState is UiState.Error) {
                Text(
                    text = (changePasswordState as UiState.Error).message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Change Password Button
            Button(
                onClick = {
                    viewModel.changePassword(currentPassword, newPassword, confirmPassword)
                },
                enabled = currentPassword.isNotBlank() &&
                         newPassword.isNotBlank() &&
                         confirmPassword.isNotBlank() &&
                         changePasswordState !is UiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (changePasswordState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Changing Password...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                } else {
                    Text(
                        text = "Change Password",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password requirements
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Password Requirements:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• At least 8 characters long\n• Contains at least one uppercase letter\n• Contains at least one lowercase letter\n• Contains at least one number",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}