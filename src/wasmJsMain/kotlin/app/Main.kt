package app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow() {
        App()
    }
}

actual fun isSystemInDarkTheme(): Boolean {
    // Web implementation
    return false
}
