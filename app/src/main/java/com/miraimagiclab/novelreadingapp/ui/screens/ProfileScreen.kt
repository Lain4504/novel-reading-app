package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ProfileViewModel
import com.miraimagiclab.novelreadingapp.ui.viewmodel.SettingsViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(
    onLoginClick: () -> Unit = {},
    onBecomeAuthorClick: () -> Unit = {},
    onMyNovelsClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val isLoggedIn = authState.isLoggedIn

    if (isLoggedIn) {
        // Show authenticated user profile
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with user info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200&h=200",
                    contentDescription = "Profile avatar",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = authState.username ?: "User",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(authState.userId ?: "", style = MaterialTheme.typography.bodySmall)
                    Text(authState.email ?: "", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Edit profile */ }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit profile")
                }
            }

            Divider()

            // Account settings
            SectionTitle("Account settings")
            ProfileMenuItem("Personal data", Icons.Default.Person) { }
            ProfileMenuItem("Account security", Icons.Default.AccountBox) { }
            ProfileMenuItem("Notification", Icons.Default.Notifications) { }
            ProfileMenuItem("Subscription", Icons.Default.ShoppingCart) { }
            ProfileMenuItem("My Booklist", Icons.Default.Check) { }
            ProfileMenuItem("Reading progression", Icons.Default.DateRange) { }
            
            // Author features
            if (!authState.roles.contains("AUTHOR")) {
                ProfileMenuItem("Become Author", Icons.Default.Edit) { 
                    onBecomeAuthorClick()
                }
            } else {
                ProfileMenuItem("My Novels", Icons.Default.Info) {
                    onMyNovelsClick()
                }
            }
            
            ProfileMenuItem("Language options", Icons.Default.Place) { }
            ProfileMenuItem("Image quality", Icons.Default.FavoriteBorder) { }
            ProfileMenuItem("Clear cache", Icons.Default.Delete) { }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // App settings
            SectionTitle("App settings")
            AppThemeMenuItem(
                title = "App Theme",
                isDarkMode = isDarkMode,
                onToggle = { settingsViewModel.toggleDarkMode() }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // About katalis
            SectionTitle("About katalis")
            ProfileMenuItem("Get to know katalis", Icons.Default.Add) { }
            ProfileMenuItem("Copyright", Icons.Default.Close) { }
        }
    } else {
        // Show beautiful login prompt centered on screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App branding
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

            // Welcome message
            Text(
                text = "Welcome to your profile",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Please login to access your personalized reading experience",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Beautiful login button
            Button(
                onClick = { onLoginClick() },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Login to Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Additional info
            Text(
                text = "Access your reading history, bookmarks, and personalized recommendations",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ProfileMenuItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null
        )
    }
}

@Composable
fun AppThemeMenuItem(
    title: String,
    isDarkMode: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = title,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = if (isDarkMode) "Dark Mode" else "Light Mode",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isDarkMode,
            onCheckedChange = { onToggle() }
        )
    }
}
