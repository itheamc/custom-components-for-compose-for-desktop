package ui.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
fun DesktopScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: DesktopScaffoldState = rememberDesktopScaffoldState(),
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    navigationRail: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (PaddingValues) -> Unit,
) {

    val child = @Composable { childModifier: Modifier ->
        Surface(modifier = childModifier, color = containerColor, contentColor = contentColor) {
            DesktopScaffoldLayout(
                topBar = topBar,
                bottomBar = bottomBar,
                navigationRail = navigationRail,
                content = content,
                snackbar = {
                    snackbarHost(scaffoldState.snackbarHostState)
                },
                fab = floatingActionButton,
            )
        }
    }

    child(modifier)

}


/**
 * ------------------------------------------------------------------------------
 * Desktop Scaffold Layout Design
 * -----------------------------------------------------------------------------
 */
@Composable
private fun DesktopScaffoldLayout(
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    navigationRail: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    fab: @Composable () -> Unit,
    snackbar: @Composable () -> Unit,
) {
    SubcomposeLayout { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        /*
        -------------------------------------------------------------------------------
        Layout Design and Content Placement Starts from here
         */
        layout(layoutWidth, layoutHeight) {
            /*
            ------------------------------------------------------
             TopBar
             */
            val topBarPlaceables =
                subcompose(slotId = DesktopScaffoldLayoutContent.TopBar) {
                    CompositionLocalProvider(
                        content = topBar
                    )
                }.map {
                    it.measure(looseConstraints)
                }

            val topBarHeight = topBarPlaceables.maxByOrNull { it.height }?.height ?: 0


            /*
            --------------------------------------------------------
             For Floating Action Button
             */
            val fabPlaceables =
                subcompose(DesktopScaffoldLayoutContent.Fab, fab).mapNotNull { measurable ->
                    measurable.measure(looseConstraints)
                        .takeIf { it.height != 0 && it.width != 0 }
                }

            val fabPlacement = if (fabPlaceables.isNotEmpty()) {
                val fabWidth = fabPlaceables.maxByOrNull { it.width }!!.width
                val fabHeight = fabPlaceables.maxByOrNull { it.height }!!.height
                // FAB distance from the left of the layout, taking into account LTR / RTL
                val fabLeftOffset = if (layoutDirection == LayoutDirection.Ltr) {
                    layoutWidth - FabSpacing.roundToPx() - fabWidth
                } else {
                    FabSpacing.roundToPx()
                }

                FabPlacement(
                    left = fabLeftOffset,
                    width = fabWidth,
                    height = fabHeight
                )
            } else {
                null
            }
            /*
            ----------------------------------------------------------
            Bottom Bar
             */
            val bottomBarPlaceables = subcompose(slotId = DesktopScaffoldLayoutContent.BottomBar) {
                CompositionLocalProvider(
                    LocalFabPlacement provides fabPlacement,
                    content = bottomBar
                )
            }.map {
                it.measure(looseConstraints)
            }

            val bottomBarHeight = bottomBarPlaceables.maxByOrNull { it.height }?.height ?: 0


            /*
            ----------------------------------------------------------
            Navigation Rail
             */
            val navigationRailPlaceables =
                subcompose(slotId = DesktopScaffoldLayoutContent.NavigationRail, content = navigationRail).map {
                    it.measure(looseConstraints)
                }

            val navigationRailWidth = navigationRailPlaceables.maxByOrNull { it.width }?.width ?: 0

            /*
            ----------------------------------------------------------
            Fab Offset Bottom
             */
            val fabOffsetFromBottom = fabPlacement?.let {
                bottomBarHeight + it.height + FabSpacing.roundToPx()
            }

            /*
            ----------------------------------------------------------
             */
            val snackbarPlaceables = subcompose(DesktopScaffoldLayoutContent.Snackbar, snackbar).map {
                it.measure(looseConstraints.copy(
                    maxWidth = (layoutWidth * 0.40).roundToInt()
                ))
            }

            val snackbarHeight = snackbarPlaceables.maxByOrNull { it.height }?.height ?: 0
            val snackbarWidth = snackbarPlaceables.maxByOrNull { it.width }?.width ?: 0

            val snackbarOffsetFromBottom = if (snackbarHeight != 0) {
                snackbarHeight + (fabOffsetFromBottom ?: bottomBarHeight)
            } else {
                0
            }

            val snackbarOffsetFromLeft = (layoutWidth/2) - (snackbarWidth/2)


            /*
            -----------------------------------------------------------------
            Content container Width and Height
             */
            val bodyContentHeight = layoutHeight - topBarHeight - bottomBarHeight
            val bodyContentWidth = layoutWidth - navigationRailWidth

            /*
            ----------------------------------------------------------------------
            Body content placeable
             */
            val bodyContentPlaceable = subcompose(DesktopScaffoldLayoutContent.MainContent) {
                val innerPadding = PaddingValues(all = 12.dp)
                content(innerPadding)
            }.map {
                it.measure(
                    looseConstraints.copy(
                        maxHeight = bodyContentHeight,
                        maxWidth = bodyContentWidth
                    )
                )
            }

            /*
            Body content placeable
             Placing to control drawing order to match default elevation of each placeable
             */
            bodyContentPlaceable.forEach {
                it.place(navigationRailWidth, topBarHeight)
            }
            /*
            Top Bar Placeable
             */
            topBarPlaceables.forEach {
                it.place(
                    x = 0,
                    y = 0
                )
            }

            /*
            Navigation Rail placeable
             */
            navigationRailPlaceables.forEach {
                it.place(
                    x = 0,
                    y = topBarHeight
                )
            }

            /*
            Bottom Bar placeable
             */
            bottomBarPlaceables.forEach {
                it.place(
                    x = navigationRailWidth,
                    y = layoutHeight - bottomBarHeight
                )
            }

            /*
            Snackbar Placeable
             */
            snackbarPlaceables.forEach {
                it.place(snackbarOffsetFromLeft, layoutHeight - snackbarOffsetFromBottom)
            }

            /*
            Fab placeable
            Explicitly not using placeRelative here as `leftOffset` already accounts for RTL
             */
            fabPlacement?.let { placement ->
                fabPlaceables.forEach {
                    it.place(placement.left, layoutHeight - fabOffsetFromBottom!!)
                }
            }
        }
    }
}


/**
 * State for [Scaffold] composable component.
 * @param drawerState the drawer state
 */
class DesktopScaffoldState(
    val drawerState: DrawerState,
    val snackbarHostState: SnackbarHostState
)

/**
 * Creates a [ScaffoldState] with the default animation clock and memorizes it.
 * @param drawerState the drawer state
 */
@Composable
fun rememberDesktopScaffoldState(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): DesktopScaffoldState = remember {
    DesktopScaffoldState(drawerState, snackbarHostState)
}

/**
 * Placement information for a [FloatingActionButton] inside a [Scaffold].
 *
 * @property left the FAB's offset from the left edge of the bottom bar, already adjusted for RTL
 * support
 * @property width the width of the FAB
 * @property height the height of the FAB
 */
@Immutable
internal class FabPlacement(
    val left: Int,
    val width: Int,
    val height: Int
)

/**
 * CompositionLocal containing a [FabPlacement] that is used to calculate the FAB bottom offset.
 */
internal val LocalFabPlacement = staticCompositionLocalOf<FabPlacement?> { null }

// FAB spacing above the bottom bar / bottom of the Scaffold
private val FabSpacing = 16.dp

private enum class DesktopScaffoldLayoutContent { TopBar, MainContent, Fab, BottomBar, NavigationRail, Snackbar }