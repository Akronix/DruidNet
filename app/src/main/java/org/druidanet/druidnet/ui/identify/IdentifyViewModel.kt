package org.druidanet.druidnet.ui.identify

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.druidanet.druidnet.network.PlantNetApiService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class IdentifyViewModel @Inject constructor(
    private val identifyService: PlantNetApiService,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _identificationStatus = MutableStateFlow<String>("")
    val identificationStatus: StateFlow<String> = _identificationStatus

    // Companion object for TAG if you prefer, or just use string literal
    companion object {
        private const val TAG = "IdentifyViewModel"
    }

    fun identify(imageBitmap: Bitmap) {
        viewModelScope.launch {
            val identifyingMsg = "Identifying..."
            _identificationStatus.value = identifyingMsg
            Log.i(TAG, identifyingMsg)

            try {
                val imageFile = bitmapToFile(imageBitmap, "plant_image.jpg")

                if (imageFile != null) {
                    val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imagePart = MultipartBody.Part.createFormData("images", imageFile.name, requestFile)

                    val organValue = "auto"
                    val organRequestBody = organValue.toRequestBody("text/plain".toMediaTypeOrNull())

                    val response = identifyService.plantIdentify(
                        images = imagePart,
                        organs = organRequestBody
                    )

                    val bestMatchName = response.results?.firstOrNull()?.species?.scientificName ?: "Unknown"
                    val successMsg = "Success: Best match - $bestMatchName. Full response: ${response.toString()}"
                    _identificationStatus.value = successMsg
                    Log.i(TAG, successMsg)
                    imageFile.delete()
                } else {
                    val convertErrorMsg = "Error: Could not convert image to file."
                    _identificationStatus.value = convertErrorMsg
                    Log.i(TAG, convertErrorMsg)
                }
            } catch (e: Exception) {
                val exceptionMsg = "Error: ${e.message}"
                _identificationStatus.value = exceptionMsg
                Log.e(TAG, exceptionMsg, e) // Log exception with error level and throwable
                // e.printStackTrace() // Log.e with throwable is generally preferred
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, fileName: String): File? {
        val cacheDir = appContext.cacheDir
        val file = File(cacheDir, fileName)
        try {
            file.createNewFile()
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            return file
        } catch (e: IOException) {
            Log.e(TAG, "Error converting bitmap to file", e) // Also log here
            // e.printStackTrace()
            return null
        }
    }
}
