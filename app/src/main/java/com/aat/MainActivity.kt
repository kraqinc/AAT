package com.aat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.aat.notification.NotificationUtils
import com.aat.ui.AatApp
import com.aat.ui.AatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        NotificationUtils.ensureChannel(this)

        setContent {
            AatTheme {
                AatApp()
            }
        }
    }
}
