package org.druidanet.druidnet.ui.identify

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.CacheControl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.druidanet.druidnet.data.PreferencesState
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.data.plant.PlantsRepository
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.network.PlantNetApiService
import org.druidanet.druidnet.network.PlantNetResponse
import org.druidanet.druidnet.network.PlantResult
import org.druidanet.druidnet.network.SpeciesInfo
import org.druidanet.druidnet.utils.compressImage
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.System.exit
import javax.inject.Inject

private const val TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class IdentifyViewModel @Inject constructor(
    private val identifyService: PlantNetApiService,
    @ApplicationContext private val appContext: Context,
    private val plantsRepository: PlantsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _identificationStatus = MutableStateFlow<String>("")
    val identificationStatus: StateFlow<String> = _identificationStatus.asStateFlow()

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _successRequest = MutableStateFlow<Boolean>(false)
    val successRequest: StateFlow<Boolean> = _successRequest.asStateFlow()

    private val _apiResponse = MutableStateFlow<PlantNetResponse?>(null)
    val apiResponse: StateFlow<PlantNetResponse?> = _apiResponse.asStateFlow()

    private val _uiState: MutableStateFlow<PlantNetResultUIState> = MutableStateFlow(PlantNetResultUIState())
    val uiState = _uiState.asStateFlow()

    private val preferencesState: StateFlow<PreferencesState> =
        userPreferencesRepository.getDisplayNameLanguagePreference.map { displayLanguage ->
            PreferencesState(displayLanguage = displayLanguage)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = runBlocking {
                PreferencesState(
                    displayLanguage = userPreferencesRepository.getDisplayNameLanguagePreference.first()
                )
            }
        )

    private val language = preferencesState.value.displayLanguage

    // The final combined UI state for the results screen
    /*
    val uiState: StateFlow<PlantNetResultUIState> = combine(
        plantDataFlow, apiResponse
    ) { plantData, response ->
        Log.i(TAG, "Plant: $plantData")
        PlantNetResultUIState(
            plant = plantData,
            score = response?.results?.firstOrNull()?.score ?: 0.0,
            similarPlants = response?.results?.drop(1) ?: emptyList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = PlantNetResultUIState()
    )
     */

    companion object {
        private const val TAG = "IdentifyViewModel"
    }

    private fun reset() {
        _apiResponse.value = null;
        _successRequest.value = false;
        _identificationStatus.value = "";
        _loading.value = false;
        _uiState.value = PlantNetResultUIState();
    }

    fun identifyOld(imageBitmap: Bitmap) {}

    fun identify(uri: Uri) {

        viewModelScope.launch {
//            _uiState.value = PlantNetResultUIState()
            reset()
            _loading.value = true // Set loading true at the start
            val identifyingMsg = "Identifying..."
            _identificationStatus.value = identifyingMsg
            Log.i(TAG, identifyingMsg)

            // Declare the file variable here to be accessible in finally
            var imageFile: File? = null

            try {
//                val imageFile = bitmapToFile(uri, "plant_image.jpg")
                imageFile = getFileFromUri(appContext, uri)

                if (imageFile != null) {

                    // Compress the image before uploading
//                    val imageSizeBc = imageFile.length() / 1024 // In KBYTES
//                    Log.d("image_before_compress", imageSizeBc.toString())
                    val compressedImage = compressImage(uri, appContext)
//                    val imageSizeAC = compressedImage.length() / 1024 // In KBYTES
//                    Log.d("image_after_compress", imageSizeAC.toString())

                    // add image to the request
                    val requestFile = compressedImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imagePart = MultipartBody.Part.createFormData("images", compressedImage.name, requestFile)

                    val organValue = "auto"
                    val organRequestBody = organValue.toRequestBody("text/plain".toMediaTypeOrNull())

                    val response = identifyService.plantIdentify(
                        images = imagePart,
                        organs = organRequestBody
                    )

                    val bestMatchName = response.results?.firstOrNull()?.species?.scientificNameWithoutAuthor ?: ""
                    val bestScore = response.results?.firstOrNull()?.score ?: 0.0

                    _uiState.value = uiState.value.copy(
                        score = bestScore,
                        latinName = bestMatchName,
                        similarPlants = response.results?.drop(1) ?: emptyList(),
                        currentPlantResult = response.results?.first()
                        )

                    if (response.results != null) {
                        val plant: Plant? = plantsRepository.searchPlant(bestMatchName, language)
                        if (plant != null) {
                            Log.i(TAG, "Plant found in database: $plant")
                            _uiState.value = uiState.value.copy(plant = plant, isInDatabase = true)
                            Log.i(TAG, "Plant: ${uiState.value.plant}")
                        }

                    }

                    val successMsg = "Success: Best match - $bestMatchName."
                    _identificationStatus.value = successMsg
                    val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
                    Log.i(TAG, "$successMsg Full response: \n${json.encodeToString(response)}")

                    _apiResponse.value = response

                    imageFile.delete()

                    _successRequest.value = true

                } else {
                    _identificationStatus.value = "Hubo un fallo con la lectura de la imagen."
                    Log.i(TAG, "Error: Could not convert image to file.")
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    _identificationStatus.value = "No se ha encontrado ninguna identificación posible para esta imagen."
                    Log.w(TAG, "No identification available for this image. (HTTP 404)", e)
                } else {
                    val httpErrorMsg = "HTTP Error: ${e.code()} - ${e.message()}"
                    _identificationStatus.value = httpErrorMsg
                    Log.e(TAG, httpErrorMsg, e)
                }
            } catch (e: IOException) {
                _identificationStatus.value = "El servicio de identificación necesita acceso a internet. Por favor, comprueba tu conexión e inténtalo de nuevo."
                Log.e(TAG, "Network error: Could not connect to the service. Please check your internet connection.", e)
            } catch (e: Exception) {
                val exceptionMsg = "Error: ${e.message ?: "An unexpected error occurred."}"
                _identificationStatus.value = "Ha ocurrido un error inesperado."
                Log.e(TAG, exceptionMsg, e)
            } finally {
                _loading.value = false // Ensure loading is set to false in all cases
                imageFile?.delete() // Safely delete the temporary file
            }
        }
    }

    fun updatePlantNetResult(newPlantName: String, newScore: Double) {
        val currentState = _uiState.value

        // Create a PlantResult for the old main plant to add to the similar list
        val oldPlantResult = currentState.currentPlantResult

        // Find the full PlantResult object for the newly selected plant.
        //    This is the missing piece.
        val newCurrentPlantResult = currentState.similarPlants.find {
            it.species?.scientificNameWithoutAuthor == newPlantName
        }

        val newSimilarPlants = currentState.similarPlants.toMutableList().apply {
            // Remove the plant that is now the main one
            removeAll { it.species?.scientificNameWithoutAuthor == newPlantName }
            // Add the old main plant to the list
            add(oldPlantResult!!)
        }.sortedByDescending { it.score ?: 0.0 }

        viewModelScope.launch {
            val newPlant: Plant? = plantsRepository.searchPlant(newPlantName, language)
            if (newPlant != null) {
                Log.i(TAG, "Plant found in database: $newPlant")
            }
            _uiState.value = uiState.value.copy(
                plant = newPlant,
                score = newScore,
                isInDatabase = newPlant != null,
                latinName = newPlantName,
                similarPlants = newSimilarPlants,
                currentPlantResult = newCurrentPlantResult
            )
        }
    }

    /**
     * Copies the data from a Uri to a temporary file in the app's cache directory.
     * This is the reliable way to get a File from a content Uri.
     */
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            // Create a temporary file in the app's cache directory
            val tempFile = File.createTempFile("plant_image_", ".jpg", context.cacheDir)
            // Ensure the file is deleted when the VM is shut down, as a fallback.
            tempFile.deleteOnExit()

            // Open an InputStream to the URI's content and a FileOutputStream to the temp file
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    // Copy the data from the input stream to the output stream
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: IOException) {
            Log.e(TAG, "Failed to create temp file from URI", e)
            null
        }
    }

    /**
     * Call this function from your UI after navigation has been handled
     * to prevent re-navigation on configuration changes.
     */
    fun onNavigationToResultsDone() {
        _apiResponse.value = null
        _successRequest.value = false
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
