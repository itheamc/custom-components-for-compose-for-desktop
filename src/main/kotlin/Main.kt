// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import navcontroller.CustomNavHost
import navcontroller.rememberNavController
import ui.navigation.ScrollbarState
import ui.navigation.rememberScrollbarState
import ui.scaffold.DesktopScaffold
import ui.scaffold.rememberDesktopScaffoldState
import ui.screens.Screen

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
@Preview
fun App(scrollbarState: ScrollbarState) {

    val scaffoldState = rememberDesktopScaffoldState()
    val scope = rememberCoroutineScope()
    val screns = Screen.values().toList()
    var selected by remember {
        mutableStateOf(Screen.HomeScreen)
    }

    val navController by rememberNavController(Screen.HomeScreen)
    val currentScreen by remember {
        navController.screen
    }

    /**
     * For Navigation Rail Animation
     */
    var visible by remember {
        mutableStateOf(true)
    }
    val width by animateDpAsState(
        targetValue = if (visible) 80.dp else 0.dp,
        animationSpec = tween(500)
    )

    val offset by animateOffsetAsState(
        targetValue = if (visible) Offset.Zero else Offset(-80f, 0f),
        animationSpec = tween(500)
    )

    LaunchedEffect(visible) {
        println(currentScreen)
    }

    MaterialTheme {
        DesktopScaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {
                        Text(currentScreen.label)
                    },
                    navigationIcon = if (!currentScreen.primary) {
                        {
                            IconButton(
                                onClick = {
                                    navController.navigateBack()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    } else null
                )
            },
            bottomBar = {
                Box(Modifier.fillMaxWidth().height(36.dp).background(color = Color.White))
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
//                        visible = !visible,
                        navController.navigate(Screen.DetailsScreen)
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    }
                )
            },
            navigationRail = {
                NavigationRail(
                    modifier = Modifier.width(width).offset(offset.x.dp, offset.y.dp),
                ) {
                    screns.forEach {
                        if (it.primary) {
                            NavigationRailItem(
                                selected = it == currentScreen,
                                onClick = {
                                    selected = it
                                    navController.navigate(it)
                                },
                                label = {
                                    Text(it.label)
                                },
                                icon = {
                                    Icon(
                                        imageVector = it.icon,
                                        contentDescription = it.label
                                    )
                                },
                                alwaysShowLabel = false
                            )
                        }
                    }
                }
            },
            verticalScrollBar = {
                VerticalScrollbar(
                    adapter = scrollbarState.scrollbarAdapter
                )
            },
            horizontalScrollBar = {
                HorizontalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollbarState.scrollState)
                )
            }
        ) {
//            Column(
//                modifier = Modifier.fillMaxWidth().verticalScroll(scrollbarState.scrollState),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                repeat(100) {
//                    Box(
//                        modifier = Modifier.fillMaxWidth().height(100.dp).background(
//                            color = Color(
//                                alpha = 255,
//                                red = Random.nextInt(256),
//                                green = Random.nextInt(256),
//                                blue = Random.nextInt(256)
//                            )
//                        )
//                    ) {
//                        Text("Hi", modifier = Modifier.combinedClickable(
//                            onClick = { visible = !visible }
//                        ))
//
//                        Text("Hi", modifier = Modifier.combinedClickable(
//                            onClick = { visible = !visible }
//                        ).align(Alignment.CenterEnd))
//                    }
//                }
            CustomNavHost(navController)
        }
    }
}


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
fun main() = application {

    val scope = rememberCoroutineScope()
    val scrollbarState = rememberScrollbarState()
    var count by remember {
        mutableStateOf(0)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Custom Composable",
        undecorated = false,
        onKeyEvent = {
            scope.launch {
                if (it.type == KeyEventType.KeyDown && it.key == Key.DirectionDown) {
                    count++
                    scrollbarState.scrollState.animateScrollBy((50 * count).toFloat())

                } else if (it.type == KeyEventType.KeyDown && it.key == Key.DirectionUp) {
                    count++
                    scrollbarState.scrollState.animateScrollBy((-50 * count).toFloat())
                } else {
                    count = 0
                }
            }
            true
        }

    ) {
        App(scrollbarState = scrollbarState)
    }

}
