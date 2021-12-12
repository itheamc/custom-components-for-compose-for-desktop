package navcontroller

import androidx.compose.runtime.Composable
import ui.screens.*

@Composable
fun CustomNavHost(
    navController: NavController,
) {
    NavigationHost(navController).Builder()
}


@Composable
fun NavigationHost.Builder() {
    composable(Screen.HomeScreen) {
        HomeScreen(it)
    }

    composable(Screen.NotificationScreen) {
        NotificationScreen(it)
    }

    composable(Screen.SettingsScreen) {
        SettingsScreen(it)
    }

    composable(Screen.DetailsScreen) {
        DetailsScreen(it)
    }
}