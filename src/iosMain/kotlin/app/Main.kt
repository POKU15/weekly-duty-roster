package app

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    App()
}

actual fun isSystemInDarkTheme(): Boolean {
    // iOS implementation - check system appearance
    return false
}
