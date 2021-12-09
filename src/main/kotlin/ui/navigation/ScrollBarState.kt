package ui.navigation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Scroll Bar State Class
 */
class ScrollbarState(
    val lazyListState: LazyListState,
    val scrollState: ScrollState,
    val lazyScrollbarAdapter: ScrollbarAdapter,
    val scrollbarAdapter: ScrollbarAdapter
)

/**
 * Composable to remember  scrollbar state
 */
@Composable
fun rememberScrollbarState(
    lazyListState: LazyListState = rememberLazyListState(),
    scrollState: ScrollState = rememberScrollState()
): ScrollbarState {
    val lazyScrollbarAdapter = rememberScrollbarAdapter(lazyListState)
    val scrollbarAdapter = rememberScrollbarAdapter(scrollState)
    return remember {
        ScrollbarState(
            lazyListState,
            scrollState,
            lazyScrollbarAdapter,
            scrollbarAdapter
        )
    }
}