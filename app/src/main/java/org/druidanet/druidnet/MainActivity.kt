package org.druidanet.druidnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.druidanet.druidnet.ui.theme.DruidNetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            DruidNetTheme {
                DruidNetApp()
            }
        }
    }
}



