package org.druidanet.druidnet.ui.identify

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CameraScreen(
    goToResultsScreen: () -> Unit,
    identifyViewModel: IdentifyViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val loading by identifyViewModel.loading.collectAsState()
    val apiResponse by identifyViewModel.apiResponse.collectAsState()

    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                capturedImageBitmap = imageBitmap
                identifyViewModel.identify(imageBitmap)
            }
        } else {
            // Handle cases where the image capture was not successful or was cancelled.
            Toast.makeText(context, "Falló abrir la cámara :(", Toast.LENGTH_SHORT).show()
            // You might want to navigate back or show a retry option here.
        }
    }

    fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePictureLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            // Display error state to the user
            Toast.makeText(context, "Camera app not found", Toast.LENGTH_LONG).show()
        }
    }


    LaunchedEffect(Unit) {
        dispatchTakePictureIntent()
    }

    LaunchedEffect(apiResponse) {
        apiResponse?.let {
            goToResultsScreen()
            identifyViewModel.onNavigationToResultsDone()
        }
    }


    if (loading) {
        LoadingScreen(imageBitmap = capturedImageBitmap)
    } else {
        // Only show status text when not loading to avoid layout conflicts
        Text(identifyViewModel.identificationStatus.collectAsState().value)
    }

}

@Composable
fun LoadingScreen(imageBitmap: Bitmap?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = "Image being identified",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        // Scrim to darken the background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Identificando...",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
