package com.miraimagiclab.novelreadingapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.miraimagiclab.novelreadingapp.navigation.Screen
import com.miraimagiclab.novelreadingapp.ui.components.EmailTextField
import com.miraimagiclab.novelreadingapp.ui.components.PasswordTextField
import com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary
import com.miraimagiclab.novelreadingapp.ui.viewmodel.AuthViewModel
import com.miraimagiclab.novelreadingapp.util.UiState
import kotlinx.coroutines.delay

@Composable
fun RegisterWithEmailScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf<String?>(null) }

    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

    // Handle register state changes
    LaunchedEffect(registerState) {
        when (registerState) {
            is UiState.Success -> {
                // Registration successful, navigate to OTP verification
                navController.navigate(Screen.OTPVerification.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
            is UiState.Error -> {
                showError = (registerState as UiState.Error).message
                delay(3000) // Show error for 3 seconds
                showError = null
                viewModel.resetRegisterState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Brand
        Text(
            text = "📚",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Novel Reading App",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error message
        showError?.let {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email field
        EmailTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Email"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Password"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password field
        PasswordTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Confirm Password"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sign up button
        Button(
            onClick = {
                when {
                    username.isBlank() -> showError = "Username is required"
                    email.isBlank() -> showError = "Email is required"
                    password.length < 8 -> showError = "Password must be at least 8 characters"
                    password != confirmPassword -> showError = "Passwords do not match"
                    else -> viewModel.register(username, email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = username.isNotBlank() && email.isNotBlank() &&
                     password.isNotBlank() && confirmPassword.isNotBlank() &&
                     registerState !is UiState.Loading
        ) {
            if (registerState is UiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Terms of Service
        Text(
            text = "By creating an account you agree with our Terms of Service, Privacy Policy, and our default Notification Settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sign in link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                Text(
                    text = "Sign in",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}