package org.druidanet.druidnet.ui.plant_sheet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.druidanet.druidnet.DruidNetApplication
import org.druidanet.druidnet.PlantSheetDestination
import org.druidanet.druidnet.data.DruidNetUiState
import org.druidanet.druidnet.data.PreferencesState
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.data.plant.PlantsRepository

private const val TIMEOUT_MILLIS = 5_000L

class PlantSheetViewModel (
    savedStatedHandle: SavedStateHandle,
    plantsRepository: PlantsRepository,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /****** VIEW MODEL CONSTRUCTOR *****/

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DruidNetApplication)
                PlantSheetViewModel(
                    this.createSavedStateHandle(),
                    application.plantsRepository,
                    application.userPreferencesRepository
                )
            }
        }
    }

    /***** Local vars *****/

    private val plantArg: String = checkNotNull(savedStatedHandle[PlantSheetDestination.plantArg])
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


    /***** UI state *****/

    // 1. A MutableStateFlow for the UI-driven state (currentSection)
    private val _currentSection = MutableStateFlow(DEFAULT_SECTION) // Initialize with a default

    // 2. The Flow from the repository
    private val plantDataFlow: Flow<PlantSheetUIState> = plantsRepository
        .getPlant(plantLatinName, language)
        .map {
            PlantSheetUIState(
                plantUiState = it,
                plantHasConfusions = it.confusions.isNotEmpty(),
                displayName = it.displayName,
            )
        }

    // 3. Combine both flows
    val uiState: StateFlow<PlantSheetUIState> = combine(
        plantDataFlow,
        _currentSection, // The flow that controls the current section,
    ) { plantSheetData, currentSection ->
        // When either flow emits a new value, this lambda is re-executed
        plantSheetData.copy(currentSection = currentSection) // Update the section in the combined state
    }.stateIn(
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