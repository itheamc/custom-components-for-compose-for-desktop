package components

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerMoveFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import kotlin.random.Random

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MakeSomeFun() {

    var pointer by remember {
        mutableStateOf(Offset.Zero)
    }

    var start by remember {
        mutableStateOf(false)
    }

    var count by remember {
        mutableStateOf(0)
    }

    val offset by animateOffsetAsState(
        targetValue = when (count) {
            0 -> Offset.Zero
            else -> Offset(
                x = Random.nextInt(1700).toFloat(),
                y = Random.nextInt(900).toFloat()
            )
        },
        animationSpec = tween(400)
    )

    val offset1 by animateOffsetAsState(
        targetValue = when (count) {
            0 -> Offset.Zero
            else -> Offset(
                x = Random.nextInt(1700).toFloat(),
                y = Random.nextInt(900).toFloat()
            )
        },
        animationSpec = tween(400)
    )

    LaunchedEffect(count) {
        delay(400)
        launch {
            if (offset.y == offset1.y && offset.x == offset1.x) {
                println("Collided")
            }
        }
        count++
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerMoveFilter(
                    onMove = {
                        pointer = it
                        true
                    },
                    onEnter = {
//                        start = true
                        println("Enter")
                        true
                    },
                    onExit = {
//                        start = false
                        println("Exit")
                        true
                    }
                )
                .combinedClickable(
                    onClick = {
                        start = false
                    },
                    onLongClick = {
                        start = true
                    },
                    onDoubleClick = {
                        start = true
                    }
                ),
            onDraw = {
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Cyan,
                            Color.Magenta,
                            Color.Red
                        )
                    ),
                    center = offset,
                    radius = (offset.y + 40f) / 4
                )
//
//
//                drawCircle(
//                    brush = Brush.linearGradient(
//                        colors = listOf(
//                            Color.Cyan,
//                            Color.Magenta,
//                            Color.Red,
//                            Color.Yellow,
//                            Color.Green
//                        )
//                    ),
//                    center = offset1,
//                    radius = (900f - offset1.y + 50f) / 5
//                )

                drawIntoCanvas {
                    it.nativeCanvas.drawString(
                        "Amit Chaudhary",
                        offset1.x,
                        offset1.y,
                        Font().apply {
                            this.size = (offset.y + 100) / 10
                        },
                        Paint().apply {
                            color = org.jetbrains.skia.Color.makeARGB(255, 200, 75, 240)
                        }
                    )
                }
            }
        )
    }
}