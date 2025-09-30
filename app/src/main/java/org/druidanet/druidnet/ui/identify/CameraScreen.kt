package org.druidanet.druidnet.ui.identify

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CameraScreen(
    goToResultsScreen: () -> Unit,
    identifyViewModel: IdentifyViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val loading = identifyViewModel.loading.collectAsState().value

    val apiResponse = identifyViewModel.apiResponse.collectAsState().value
    val hasAPIResponded = apiResponse != null

    // var capturedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) } // Example if you want to display the image

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                // You can use the imageBitmap here. For example, assign it to a state variable:
                // capturedImageBitmap = imageBitmap.asImageBitmap()
                // Or send it to a ViewModel, etc.
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
                // Show loading screen
                identifyViewModel.identify(imageBitmap)
            }
        } else {
            // Handle cases where the image capture was not successful or was cancelled.
            Toast.makeText(context, " Falló abrir la cámara :(", Toast.LENGTH_SHORT).show()
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

    LaunchedEffect(hasAPIResponded) {
        apiResponse?.let { responseData ->
            goToResultsScreen()
            identifyViewModel.onNavigationToResultsDone()
        }

    }


    if (loading)
        LoadingScreen()

    Text(identifyViewModel.identificationStatus.collectAsState().value)

}

@Composable
fun LoadingScreen() {

}