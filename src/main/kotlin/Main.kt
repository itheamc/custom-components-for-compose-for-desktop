// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.scaffold.DesktopScaffold
import ui.scaffold.rememberDesktopScaffoldState

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
@Preview
fun App() {

    val scaffoldState = rememberDesktopScaffoldState()
    val scope = rememberCoroutineScope()

    /**
     * For Navigation Rail Animation
     */
    var visible by remember {
        mutableStateOf(true)
    }
    val width by animateDpAsState(
        targetValue = if (visible) 80.dp else 12.dp,
        animationSpec = tween(500)
    )

    val offset by animateOffsetAsState(
        targetValue = if (visible) Offset.Zero else Offset(-80f, 0f),
        animationSpec = tween(500)
    )

    DesktopMaterialTheme {
        DesktopScaffold(
            scaffoldState = scaffoldState,
//            topBar = {
//                Box(Modifier.fillMaxWidth().height(80.dp).background(color = Color.Cyan))
//            },
//            bottomBar = {
//                Box(Modifier.fillMaxWidth().height(60.dp).background(color = Color.Red))
//            },
            navigationRail = {
//                AnimatedVisibility(
//                    modifier = Modifier.width(navrailWidth),
//                    visible = visible,
//                    enter = slideInHorizontally(
//                        animationSpec = tween(500)
//                    ) + fadeIn(animationSpec = tween(500)),
//                    exit = slideOutHorizontally(
//                        animationSpec = tween(500)
//                    ) + fadeOut(animationSpec = tween(500))
//                ) {
//
//                }
                NavigationRail(
                    modifier = Modifier.width(width).offset(offset.x.dp, offset.y.dp),
                ) {
                    NavigationRailItem(
                        selected = true,
                        onClick = {
                            scope.launch { scaffoldState.snackbarHostState.showSnackbar("Hello Snackbar") }
                        },
                        label = {
                            Text("Home")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = null
                            )
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Hello Desktop", modifier = Modifier.clickable {
                    visible = !visible
                })
            }
        }
    }
}


fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "Custom Composable"
    ) {
        App()
    }

}
