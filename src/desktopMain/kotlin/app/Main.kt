package app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Dunkwa FPU Weekly Duty Roster"
    ) {
        App()
    }
}

actual fun isSystemInDarkTheme(): Boolean {
    // Desktop implementation
    return false
}
