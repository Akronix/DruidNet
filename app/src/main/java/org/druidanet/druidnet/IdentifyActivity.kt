package org.druidanet.druidnet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.druidanet.druidnet.ui.identify.IdentifyScreen
import org.druidanet.druidnet.ui.identify.IdentifyViewModel
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@AndroidEntryPoint
class IdentifyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageSrc: Uri? = if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, )
        } else {
            null
        }

        enableEdgeToEdge()
        setContent {
            DruidNetTheme {
                val identifyViewModel: IdentifyViewModel = hiltViewModel()

                LaunchedEffect(imageSrc) {
                    imageSrc?.let {
                        identifyViewModel.identify(it)
                    }
                }

                IdentifyScreen(
                    identifyViewModel = identifyViewModel,
                    goToPlantSheet = { plant, section ->
                        val deepLinkUri = Uri.parse("druidnet://druidanet.org/plant_sheet/${plant.latinName}?section=$section")
                        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri)
                        startActivity(intent)
                        finish()
                    },
                    onPressBackButton = {
                        finish()
                    },
                    navigateToCameraScreen = {
//                        val intent = Intent(this, MainActivity::class.java)
//                        startActivity(intent)
                        finish()
                    },
                    innerPadding = androidx.compose.foundation.layout.PaddingValues(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
