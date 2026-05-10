package app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

actual fun isSystemInDarkTheme(): Boolean {
    // Android implementation
    return androidx.compose.foundation.isSystemInDarkTheme()
}
