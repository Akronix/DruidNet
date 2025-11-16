package org.druidanet.druidnet.ui.identify

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import org.druidanet.druidnet.utils.compressImage
import org.druidanet.druidnet.utils.getFileFromUri
import retrofit2.HttpException
import java.io.File
import java.io.IOException
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

    private val _error = MutableStateFlow<Boolean>(false)
    val error: StateFlow<Boolean> = _error.asStateFlow()

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

    companion object {
        private const val TAG = "IdentifyViewModel"
    }

    private fun reset() {
        _apiResponse.value = null;
        _successRequest.value = false;
        _identificationStatus.value = "";
        _loading.value = false;
        _error.value = false;
        _uiState.value = PlantNetResultUIState();
    }

    fun identify(uri: Uri) {

        viewModelScope.launch {
            reset()

            val originalImageFile = try {
                getFileFromUri(appContext, uri, prefix = "plant_image_", suffix = ".jpg")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to create temp file from URI", e)
                _error.value = true
                return@launch
            }

            var compressedImage: File

            // First, we compress the image and save the result in the uiState
            if (originalImageFile != null) {
                _loading.value = true // Set loading true at the start
                _identificationStatus.value = "Comprimiendo..."
                Log.i(TAG, "Compressing...")
                // Compress the image before uploading
                //                    val imageSizeBc = originalImageFile.length() / 1024 // In KBYTES
                //                    Log.d("image_before_compress", imageSizeBc.toString())
                compressedImage = compressImage(uri, appContext)
                _uiState.value = uiState.value.copy(imageForIdentification = compressedImage)
                //                    val imageSizeAC = compressedImage.length() / 1024 // In KBYTES
                //                    Log.d("image_after_compress", imageSizeAC.toString())

            } else { // Missing input image file :/
                _identificationStatus.value = "Hubo un fallo con la lectura de la imagen."
                Log.e(TAG, "Error: Could not convert image to file.")
                _loading.value = false
                _error.value = true
                return@launch
            }

            // Second, we start the identifying API request
            val identifyingMsg = "Identificando..."
            _identificationStatus.value = identifyingMsg
            Log.i(TAG, "Identifying...")

            try {
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
//                            Log.i(TAG, "Plant found in database!")
                            _uiState.value = uiState.value.copy(plant = plant, isInDatabase = true)
//                            Log.i(TAG, "Plant: ${uiState.value.plant}")
                        }

                    }

                    val successMsg = "Success: Best match - $bestMatchName."
                    _identificationStatus.value = successMsg
//                    val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
//                    Log.i(TAG, "$successMsg Full response: \n${json.encodeToString(response)}")

                    _apiResponse.value = response

                    originalImageFile.delete()

                    _successRequest.value = true


                } catch (e: HttpException) {
                    _error.value = true
                    if (e.code() == 404) {
                        _identificationStatus.value = "No se ha encontrado ninguna identificación posible para esta imagen."
                        Log.e(TAG, "No identification available for this image. (HTTP 404)", e)
                    } else {
                        val httpErrorMsg = "HTTP Error: ${e.code()} - ${e.message()}"
                        _identificationStatus.value = httpErrorMsg
                        Log.e(TAG, httpErrorMsg, e)
                    }
                } catch (e: IOException) {
                    _error.value = true
                    _identificationStatus.value = "El servicio de identificación necesita acceso a internet. Por favor, comprueba tu conexión e inténtalo de nuevo."
                    Log.e(TAG, "Network error: Could not connect to the service. Please check your internet connection.", e)
                } catch (e: Exception) {
                    _error.value = true
                    val exceptionMsg = "Error: ${e.message ?: "An unexpected error occurred."}"
                    _identificationStatus.value = "Ha ocurrido un error inesperado."
                    Log.e(TAG, exceptionMsg, e)
                } finally {
                    _loading.value = false // Ensure loading is set to false in all cases
                    originalImageFile.delete() // Safely delete the temporary file
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
//            if (newPlant != null) {
//                Log.i(TAG, "Plant found in database: $newPlant")
//            }
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
     * Call this function from your UI after navigation has been handled
     * to prevent re-navigation on configuration changes.
     */
    fun onNavigationToResultsDone() {
        _apiResponse.value = null
        _successRequest.value = false
    }

}
