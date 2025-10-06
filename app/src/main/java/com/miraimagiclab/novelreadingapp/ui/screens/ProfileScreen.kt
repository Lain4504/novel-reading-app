package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
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
                    text = "Cheyenne Curtis",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text("022-23093", style = MaterialTheme.typography.bodySmall)
                Text("Shinei.Nouzen@nordlicht.com", style = MaterialTheme.typography.bodySmall)
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
        ProfileMenuItem("Language options", Icons.Default.Place) { }
        ProfileMenuItem("Image quality", Icons.Default.FavoriteBorder) { }
        ProfileMenuItem("Clear cache", Icons.Default.Delete) { }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // About katalis
        SectionTitle("About katalis")
        ProfileMenuItem("Get to know katalis", Icons.Default.Add) { }
        ProfileMenuItem("Copyright", Icons.Default.Close) { }
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
