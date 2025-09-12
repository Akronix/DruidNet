package org.druidanet.druidnet.ui.plant_sheet

// import androidx.lifecycle.ViewModelProvider // REMOVED
// import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY // REMOVED
// import androidx.lifecycle.createSavedStateHandle // REMOVED (Hilt handles SavedStateHandle)
// import androidx.lifecycle.viewmodel.initializer // REMOVED
// import androidx.lifecycle.viewmodel.viewModelFactory // REMOVED
// import org.druidanet.druidnet.DruidNetApplication // REMOVED
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
import kotlinx.coroutines.runBlocking
import org.druidanet.druidnet.data.PreferencesState
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.data.plant.PlantsRepository
import org.druidanet.druidnet.navigation.PlantSheetDestination
import javax.inject.Inject

private const val TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class PlantSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, // Hilt provides this
    plantsRepository: PlantsRepository, // Hilt provides this
    userPreferencesRepository: UserPreferencesRepository // Hilt provides this
) : ViewModel() {

    /***** Local vars *****/

    private val plantArg: String = checkNotNull(savedStateHandle[PlantSheetDestination.plantArg])
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

    // 1. A MutableStateFlow for the UI-driven state (currentSection)
    private val _currentSection = MutableStateFlow(DEFAULT_SECTION) // Initialize with a default

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
    ) { plantSheetData, currentSection ->
        // When either flow emits a new value, this lambda is re-executed
        plantSheetData.copy(currentSection = currentSection) // Update the section in the combined state
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

}