package ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val label: String,
    val icon: ImageVector,
    val primary: Boolean = false
) {
    HomeScreen(
        label = "Home",
        icon = Icons.Filled.Home,
        primary = true
    ),
    NotificationScreen(
        label = "Notifications",
        icon = Icons.Filled.Notifications,
        primary = true
    ),
    SettingsScreen(
        label = "Settings",
        icon = Icons.Filled.Settings,
        primary = true
    ),
    DetailsScreen(
        label = "Details",
        icon = Icons.Filled.AddCircle
    )
}