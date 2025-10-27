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
import androidx.navigation.NavController
import com.miraimagiclab.novelreadingapp.navigation.Screen
import com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary

@Composable
fun RegisterScreen(
    navController: NavController
) {
    val scrollState = rememberScrollState()

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
            text = "Create an account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sign up with email button
        OutlinedButton(
            onClick = { navController.navigate(Screen.RegisterWithEmail.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp
            )
        ) {
            Text(
                text = "Sign up with Email",
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Terms of Service
        Text(
            text = "By creating an account you agree with our Terms of Service, Privacy Policy, and our default Notification Settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
