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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.druidanet.druidnet.network.PlantNetApiService
import org.druidanet.druidnet.network.PlantNetResponse
import retrofit2.HttpException
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
    val identificationStatus: StateFlow<String> = _identificationStatus.asStateFlow()

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _apiResponse = MutableStateFlow<PlantNetResponse?>(null)
    val apiResponse: StateFlow<PlantNetResponse?> = _apiResponse.asStateFlow()

    companion object {
        private const val TAG = "IdentifyViewModel"
    }

    fun identify(imageBitmap: Bitmap) {
        viewModelScope.launch {
            _loading.value = true // Set loading true at the start
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
                    val successMsg = "Success: Best match - $bestMatchName."
                    _identificationStatus.value = successMsg
                    Log.i(TAG, "$successMsg Full response: ${response.toString()}")

                    _apiResponse.value = response

                    imageFile.delete()
                } else {
                    val convertErrorMsg = "Error: Could not convert image to file."
                    _identificationStatus.value = convertErrorMsg
                    Log.i(TAG, convertErrorMsg)
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    val notFoundMsg = "No identification available for this image."
                    _identificationStatus.value = notFoundMsg
                    Log.w(TAG, "$notFoundMsg (HTTP 404)", e)
                } else {
                    val httpErrorMsg = "HTTP Error: ${e.code()} - ${e.message()}"
                    _identificationStatus.value = httpErrorMsg
                    Log.e(TAG, httpErrorMsg, e)
                }
            } catch (e: IOException) {
                val networkErrorMsg = "Network error: Could not connect to the service. Please check your internet connection."
                _identificationStatus.value = networkErrorMsg
                Log.e(TAG, networkErrorMsg, e)
            } catch (e: Exception) {
                val exceptionMsg = "Error: ${e.message ?: "An unexpected error occurred."}"
                _identificationStatus.value = exceptionMsg
                Log.e(TAG, exceptionMsg, e)
            } finally {
                _loading.value = false // Ensure loading is set to false in all cases
            }
        }
    }

    /**
     * Call this function from your UI after navigation has been handled
     * to prevent re-navigation on configuration changes.
     */
    fun onNavigationToResultsDone() {
        _apiResponse.value = null
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
            Log.e(TAG, "Error converting bitmap to file", e)
            return null
        }
    }
}
