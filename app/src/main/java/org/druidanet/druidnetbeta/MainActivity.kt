package org.druidanet.druidnetbeta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.druidanet.druidnetbeta.ui.theme.DruidNetBetaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DruidNetBetaTheme {
                DruidNetApp()
            }
        }
    }
}



