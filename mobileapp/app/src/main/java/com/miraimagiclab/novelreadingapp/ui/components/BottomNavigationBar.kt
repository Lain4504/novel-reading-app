package com.miraimagiclab.novelreadingapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.miraimagiclab.novelreadingapp.navigation.Screen

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isLoggedIn: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bottomNavItems = if (isLoggedIn) {
        // When logged in: Explore, Home, Library, Profile
        listOf(
            BottomNavItem(
                route = Screen.Explore.route,
                title = "Explore",
                icon = Icons.Outlined.Explore,
                selectedIcon = Icons.Filled.Explore
            ),
            BottomNavItem(
                route = Screen.Home.route,
                title = "Home",
                icon = Icons.Outlined.Home,
                selectedIcon = Icons.Filled.Home
            ),
            BottomNavItem(
                route = Screen.BookList.route,
                title = "Library",
                icon = Icons.Outlined.LibraryBooks,
                selectedIcon = Icons.Filled.LibraryBooks
            ),
            BottomNavItem(
                route = Screen.Profile.route,
                title = "Profile",
                icon = Icons.Outlined.Person,
                selectedIcon = Icons.Filled.Person
            )
        )
    } else {
        // When not logged in: Explore, Home, Profile
        listOf(
            BottomNavItem(
                route = Screen.Explore.route,
                title = "Explore",
                icon = Icons.Outlined.Explore,
                selectedIcon = Icons.Filled.Explore
            ),
            BottomNavItem(
                route = Screen.Home.route,
                title = "Home",
                icon = Icons.Outlined.Home,
                selectedIcon = Icons.Filled.Home
            ),
            BottomNavItem(
                route = Screen.Profile.route,
                title = "Profile",
                icon = Icons.Outlined.Person,
                selectedIcon = Icons.Filled.Person
            )
        )
    }

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
