package com.miraimagiclab.novelreadingapp.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ProfileViewModel
import com.miraimagiclab.novelreadingapp.ui.viewmodel.SettingsViewModel
import com.miraimagiclab.novelreadingapp.util.UiState
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(
    onLoginClick: () -> Unit = {},
    onBecomeAuthorClick: () -> Unit = {},
    onMyNovelsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val userDetails by viewModel.userDetails.collectAsState()
    val updateUserState by viewModel.updateUserState.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val isLoggedIn = authState.isLoggedIn
    
    var showEditDialog by remember { mutableStateOf(false) }

    // Load user details when screen appears
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.loadUserDetails()
        }
    }

    // Handle update success
    LaunchedEffect(updateUserState) {
        if (updateUserState is UiState.Success) {
            showEditDialog = false
            viewModel.loadUserDetails() // Reload user details
            viewModel.resetUpdateState()
        }
    }

    if (isLoggedIn) {
        // Show authenticated user profile
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with user info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                val currentUser = (userDetails as? UiState.Success)?.data
                AsyncImage(
                    model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200&h=200",
                    contentDescription = "Profile avatar",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = currentUser?.displayName ?: authState.username ?: "User",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (currentUser?.bio?.isNotEmpty() == true) {
                        Text(
                            text = currentUser.bio,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                    Text(authState.email ?: "", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showEditDialog = true }) {
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
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Logout section
            SectionTitle("Account")
            ProfileMenuItem("Logout", Icons.Default.ExitToApp) {
                onLogoutClick()
            }
        }

        // Edit Profile Dialog
        if (showEditDialog) {
            EditProfileDialog(
                userDetails = (userDetails as? UiState.Success)?.data,
                isLoading = updateUserState is UiState.Loading,
                uploadImageState = viewModel.uploadImageState.collectAsState().value,
                onDismiss = { showEditDialog = false },
                onSave = { avatarUrl, backgroundUrl, bio, displayName ->
                    viewModel.updateUserProfile(avatarUrl, backgroundUrl, bio, displayName)
                },
                onUploadImage = { imageFile ->
                    viewModel.uploadImage(imageFile)
                },
                onResetUploadState = {
                    viewModel.resetUploadImageState()
                }
            )
        }

        // Show error if update fails
        if (updateUserState is UiState.Error) {
            LaunchedEffect(updateUserState) {
                // You could show a Snackbar here
            }
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

// Helper function to convert URI to File
fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun EditProfileDialog(
    userDetails: com.miraimagiclab.novelreadingapp.data.remote.dto.UserDto?,
    isLoading: Boolean,
    uploadImageState: UiState<String>,
    onDismiss: () -> Unit,
    onSave: (String?, String?, String?, String?) -> Unit,
    onUploadImage: (File) -> Unit,
    onResetUploadState: () -> Unit
) {
    val context = LocalContext.current
    var avatarUrl by remember { mutableStateOf(userDetails?.avatarUrl ?: "") }
    var backgroundUrl by remember { mutableStateOf(userDetails?.backgroundUrl ?: "") }
    var bio by remember { mutableStateOf(userDetails?.bio ?: "") }
    var displayName by remember { mutableStateOf(userDetails?.displayName ?: "") }
    
    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBackgroundUri by remember { mutableStateOf<Uri?>(null) }
    var uploadingImageType by remember { mutableStateOf<String?>(null) } // "avatar" or "background"
    
    // Handle upload image success
    LaunchedEffect(uploadImageState) {
        if (uploadImageState is UiState.Success) {
            when (uploadingImageType) {
                "avatar" -> {
                    avatarUrl = uploadImageState.data
                    selectedAvatarUri = null
                }
                "background" -> {
                    backgroundUrl = uploadImageState.data
                    selectedBackgroundUri = null
                }
            }
            uploadingImageType = null
            onResetUploadState()
        }
    }
    
    // Avatar image picker
    val avatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedAvatarUri = it
            uploadingImageType = "avatar"
            val file = uriToFile(context, it)
            if (file != null) {
                onUploadImage(file)
            }
        }
    }
    
    // Background image picker
    val backgroundLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedBackgroundUri = it
            uploadingImageType = "background"
            val file = uriToFile(context, it)
            if (file != null) {
                onUploadImage(file)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                // Display Name
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Bio
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Avatar URL/Picker
                Text(
                    text = "Avatar Image",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { avatarLauncher.launch("image/*") },
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick Image")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (selectedAvatarUri != null) {
                        AsyncImage(
                            model = selectedAvatarUri,
                            contentDescription = "Selected avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                    } else if (avatarUrl.isNotEmpty()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Current avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { 
                        avatarUrl = it
                        selectedAvatarUri = null // Clear selected URI if user types URL
                    },
                    label = { Text("Or enter URL") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    placeholder = { Text("https://example.com/avatar.jpg") }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Background URL/Picker
                Text(
                    text = "Background Image",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { backgroundLauncher.launch("image/*") },
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick Image")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (selectedBackgroundUri != null) {
                        AsyncImage(
                            model = selectedBackgroundUri,
                            contentDescription = "Selected background",
                            modifier = Modifier
                                .width(80.dp)
                                .height(48.dp)
                                .clip(MaterialTheme.shapes.small)
                        )
                    } else if (backgroundUrl.isNotEmpty()) {
                        AsyncImage(
                            model = backgroundUrl,
                            contentDescription = "Current background",
                            modifier = Modifier
                                .width(80.dp)
                                .height(48.dp)
                                .clip(MaterialTheme.shapes.small)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = backgroundUrl,
                    onValueChange = { 
                        backgroundUrl = it
                        selectedBackgroundUri = null // Clear selected URI if user types URL
                    },
                    label = { Text("Or enter URL") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    placeholder = { Text("https://example.com/background.jpg") }
                )

                // Show current user info for reference
                if (userDetails != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Account Info (Read Only)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Username: ${userDetails.username}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Email: ${userDetails.email}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        avatarUrl.ifBlank { null },
                        backgroundUrl.ifBlank { null },
                        bio.ifBlank { null },
                        displayName.ifBlank { null }
                    )
                },
                enabled = !isLoading && uploadImageState !is UiState.Loading
            ) {
                if (isLoading || uploadImageState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (uploadImageState is UiState.Loading) "Uploading..." else "Saving...")
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading && uploadImageState !is UiState.Loading
            ) {
                Text("Cancel")
            }
        }
    )
}
