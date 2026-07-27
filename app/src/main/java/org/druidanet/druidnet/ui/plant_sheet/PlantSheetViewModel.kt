package org.druidanet.druidnet.ui.plant_sheet

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.druidanet.druidnet.data.PreferencesState
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.data.plant.PlantsRepository
import org.druidanet.druidnet.navigation.PlantSheetDestination
import org.druidanet.druidnet.network.iNaturalistApiService
import javax.inject.Inject

private const val TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class PlantSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, // Hilt provides this
    plantsRepository: PlantsRepository, // Hilt provides this
    userPreferencesRepository: UserPreferencesRepository, // Hilt provides this
    private val iNaturalistService: iNaturalistApiService, // Hilt provides this
) : ViewModel() {

    /***** Local vars *****/

    private val plantArg: String = checkNotNull(savedStateHandle[PlantSheetDestination.plantArg])
    private val sectionArg: String? = savedStateHandle[PlantSheetDestination.sectionArg]
    private val plantLatinName = plantArg.replace('_', ' ')

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


    /* The following should be inside the UI state, but I couldn't let it working :( */
    /* I tried combining flows but plantDataFlow doesn't output when the result is null,
    * so that this flow would never return the value of isPlantInDatabase = false, therefore
    * the UI wouldn't get advised to change */
    private val isPlantInDatabaseFlow: Flow<Boolean> =
        plantsRepository.isPlantInDatabase(plantLatinName)

    val isPlantInDatabase: StateFlow<Boolean> = isPlantInDatabaseFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = true
    )

    /***** UI state *****/

    /* It could be interesting to change the whole implementation and move from using Flows
       to using suspend and coroutines. May it has more sense, and then we could have a
       Loading and Error state. But we would lose the Flow capabilities of course.
       To do that transition, follow this example: https://github.com/android/compose-samples/blob/73b3a51e06a6520efb5b4931e71b771d257bf1dd/JetNews/app/src/main/java/com/example/jetnews/ui/home/HomeViewModel.kt#L150
     */

    private val _onlineImages = MutableStateFlow<List<String>>(emptyList())

    private val initialSection = try {
        sectionArg?.let { PlantSheetSection.valueOf(it.uppercase()) } ?: DEFAULT_SECTION
    } catch (e: IllegalArgumentException) {
        DEFAULT_SECTION
    }

    // 1. A MutableStateFlow for the UI-driven state (currentSection)
    private val _currentSection = MutableStateFlow(initialSection) // Initialize with a default

    // 2. The Flow from the repository
    private val plantDataFlow: Flow<PlantSheetUIState> = plantsRepository
        .getPlant(plantLatinName, language)
        .map {
            PlantSheetUIState(
                plantUiState = it,
                plantHasConfusions = it.confusions.isNotEmpty(),
                displayName = it.displayName
            )
        }

    // 3. Combine current section and plantData flows
    val uiState: StateFlow<PlantSheetUIState> = combine(
        plantDataFlow,
        _currentSection, // The flow that controls the current section,
        _onlineImages // The flow for the iNaturalist online images
    ) { plantSheetData, currentSection, onlineImages ->
        // When either flow emits a new value, this lambda is re-executed
        plantSheetData.copy(
            currentSection = currentSection, // Update the section in the combined state
            onlineImages = onlineImages) // Map onlineImages to the UI state
    }.
    stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = PlantSheetUIState() // Ensure initialValue also has default section
    )

    fun changeSection(newSection: PlantSheetSection) {
        if (newSection != _currentSection.value) // Check against _currentSection's value
            _currentSection.value = newSection // Update the _currentSection MutableStateFlow directly
        // This will trigger the combine to re-emit
    }

    /** NETWORK FUNCTIONS **/

    fun getOnlineImages(latinName: String) {

        var queryName : String
        var rank : String
        if (latinName.contains("spp.")) {
            queryName = latinName.substringBefore(" spp.")
            rank = "genus"
        } else {
            queryName = latinName
            rank = "species"
        }

        viewModelScope.launch {

            try {
                var resp = iNaturalistService.retrieveTaxaRecord(queryName, rank)
                val results = resp.body()?.get("results")?.jsonArray

                Log.i("DRUIDNET-INAT", resp.toString())
                Log.i("DRUIDNET-INAT", results.toString())

                var foundTaxa = false;
                var taxonId: Int = 0
                var i = 0;
                if (results != null) {
                    while (!foundTaxa && i < results.size) {
                        foundTaxa = results[i].jsonObject["name"]?.jsonPrimitive?.content == queryName
                        if (!foundTaxa)
                            i++;
                        else
                            taxonId = results[i].jsonObject["id"]?.jsonPrimitive?.int!!
                    }
                    if (foundTaxa && taxonId != 0) {

                        Log.i("DRUIDNET-INAT", taxonId.toString())

                        resp = iNaturalistService.retrieveImages(taxonId)
                        val resultsPhotos = resp.body()?.get("results")?.jsonArray
                            ?.mapNotNull { resultObj ->
                                resultObj.jsonObject["photos"]?.jsonArray?.get(0)?.jsonObject?.get("url")?.jsonPrimitive?.content
                            }

                        //                    val photosURLsSquare = results?.get(0)?.jsonObject?.get("photos")?.jsonArray
                        //                        ?.mapNotNull { photoObj ->
                        //                            photoObj.jsonObject["url"]?.jsonPrimitive?.content
                        //                        }

                        val photos = resultsPhotos
                            ?.map { it.replace("square", "large") }
                            ?: emptyList()

                        Log.i("DRUIDNET-INAT", resultsPhotos.toString())
                        Log.i("DRUIDNET-INAT", photos.toString())
                        _onlineImages.value = photos
                    }
                }

                if (!foundTaxa) {
                    Log.i("DRUIDNET-INAT", "No found taxon in iNaturalist")
                }

            } catch (e: Exception) {
                Log.e("DRUIDNET-ERROR", "Failed to fetch images", e)
            }
        }

    }


}