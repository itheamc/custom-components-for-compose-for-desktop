package navcontroller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import ui.screens.Screen


/**
 * NavController
 */
class NavController(
    private var currentScreen: Screen,
    private var lastScreens: MutableSet<Screen>,
) {

    var screen: MutableState<Screen> = mutableStateOf(currentScreen)

    fun navigate(scr: Screen) {
        if (scr != currentScreen) {
            if (lastScreens.contains(currentScreen) && currentScreen != Screen.HomeScreen) {
                lastScreens.remove(currentScreen)
            }

            if (scr == Screen.HomeScreen) {
                lastScreens = mutableSetOf()
            } else {
                lastScreens.add(currentScreen)
            }

            currentScreen = scr
            screen.value = scr
        }
        println(lastScreens)
    }

    fun navigateBack() {
        if (lastScreens.isNotEmpty()) {
            currentScreen = lastScreens.last()
            screen.value = currentScreen
            lastScreens.remove(currentScreen)
        }
    }

}


@Composable
fun rememberNavController(
    currentScreen: Screen = Screen.HomeScreen,
    lastScreens: MutableSet<Screen> = mutableSetOf()
): MutableState<NavController> = rememberSaveable {
    mutableStateOf(NavController(currentScreen, lastScreens))
}


/**
 * Navigation Host
 */
class NavigationHost(
    val navController: NavController,
    val contents: @Composable Builder.() -> Unit
) {

    @Composable
    fun build() {
        Builder().render()
    }

    inner class Builder(val navController: NavController = this@NavigationHost.navController) {
        @Composable
        fun render() {
            this@NavigationHost.contents(this)
        }
    }
}


@Composable
fun NavigationHost.Builder.composable(
    route: Screen,
    content: @Composable (NavController) -> Unit
) {
    if (navController.screen.value == route) {
        content(navController)
    }
}