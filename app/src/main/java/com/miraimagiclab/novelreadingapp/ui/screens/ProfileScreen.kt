package com.miraimagiclab.novelreadingapp.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.miraimagiclab.novelreadingapp.ui.theme.CustomShapes
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

            HorizontalDivider()

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

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // App settings
            SectionTitle("App settings")
            AppThemeMenuItem(
                title = "App Theme",
                isDarkMode = isDarkMode,
                onToggle = { settingsViewModel.toggleDarkMode() }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // About katalis
            SectionTitle("About katalis")
            ProfileMenuItem("Get to know katalis", Icons.Default.Add) { }
            ProfileMenuItem("Copyright", Icons.Default.Close) { }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Logout section
            SectionTitle("Account")
            ProfileMenuItem("Logout", Icons.Default.ExitToApp) {
                onLogoutClick()
            }
            
            Divider()
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = CustomShapes.dialogShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.White // White background
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isLoading && uploadImageState !is UiState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF666666)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFEEEEEE))

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Display Name
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Display Name",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A)
                        )
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            placeholder = { Text("Enter your display name", color = Color(0xFF999999)) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary,
                                unfocusedBorderColor = Color(0xFFDDDDDD),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color(0xFFF5F5F5),
                                focusedTextColor = Color(0xFF1A1A1A),
                                unfocusedTextColor = Color(0xFF1A1A1A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Bio
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Bio",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A)
                        )
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            placeholder = { Text("Tell us about yourself...", color = Color(0xFF999999)) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            minLines = 3,
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary,
                                unfocusedBorderColor = Color(0xFFDDDDDD),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color(0xFFF5F5F5),
                                focusedTextColor = Color(0xFF1A1A1A),
                                unfocusedTextColor = Color(0xFF1A1A1A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Avatar Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Avatar Image",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar preview
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedAvatarUri != null || avatarUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = selectedAvatarUri ?: avatarUrl,
                                        contentDescription = "Avatar",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = Color(0xFFCCCCCC),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            
                            // Pick button
                            OutlinedButton(
                                onClick = { avatarLauncher.launch("image/*") },
                                enabled = !isLoading,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.5.dp,
                                    color = com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Choose Image", fontWeight = FontWeight.Medium)
                            }
                        }
                        
                        OutlinedTextField(
                            value = avatarUrl,
                            onValueChange = { 
                                avatarUrl = it
                                selectedAvatarUri = null
                            },
                            placeholder = { Text("Or paste image URL", color = Color(0xFF999999)) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary,
                                unfocusedBorderColor = Color(0xFFDDDDDD),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color(0xFFF5F5F5),
                                focusedTextColor = Color(0xFF1A1A1A),
                                unfocusedTextColor = Color(0xFF1A1A1A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Background Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Background Image",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Background preview
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedBackgroundUri != null || backgroundUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = selectedBackgroundUri ?: backgroundUrl,
                                        contentDescription = "Background",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFFCCCCCC),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            
                            // Pick button
                            OutlinedButton(
                                onClick = { backgroundLauncher.launch("image/*") },
                                enabled = !isLoading,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.5.dp,
                                    color = com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Choose Image", fontWeight = FontWeight.Medium)
                            }
                        }
                        
                        OutlinedTextField(
                            value = backgroundUrl,
                            onValueChange = { 
                                backgroundUrl = it
                                selectedBackgroundUri = null
                            },
                            placeholder = { Text("Or paste image URL", color = Color(0xFF999999)) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary,
                                unfocusedBorderColor = Color(0xFFDDDDDD),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color(0xFFF5F5F5),
                                focusedTextColor = Color(0xFF1A1A1A),
                                unfocusedTextColor = Color(0xFF1A1A1A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Account Info (Read Only)
                    if (userDetails != null) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFEEEEEE)
                        )
                        
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Account Information",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF666666)
                            )
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFFF9F9F9),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF999999),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Username: ${userDetails.username}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF666666)
                                )
                            }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFFF9F9F9),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Color(0xFF999999),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Email: ${userDetails.email}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF666666)
                                )
                            }
                        }
                    }
                }

                // Footer with action buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel Button
                        OutlinedButton(
                            onClick = onDismiss,
                            enabled = !isLoading && uploadImageState !is UiState.Loading,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF666666)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.5.dp,
                                color = Color(0xFFDDDDDD)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 14.dp)
                        ) {
                            Text(
                                "Cancel",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        // Save Button
                        Button(
                            onClick = {
                                onSave(
                                    avatarUrl.ifBlank { null },
                                    backgroundUrl.ifBlank { null },
                                    bio.ifBlank { null },
                                    displayName.ifBlank { null }
                                )
                            },
                            enabled = !isLoading && uploadImageState !is UiState.Loading,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.miraimagiclab.novelreadingapp.ui.theme.GreenPrimary,
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFFCCCCCC),
                                disabledContentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 14.dp)
                        ) {
                            if (isLoading || uploadImageState is UiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (uploadImageState is UiState.Loading) "Uploading..." else "Saving...",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    "Save Changes",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
