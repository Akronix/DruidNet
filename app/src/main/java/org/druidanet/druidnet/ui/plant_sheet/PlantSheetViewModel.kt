package org.druidanet.druidnet.ui.plant_sheet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import org.druidanet.druidnet.DruidNetApplication
import org.druidanet.druidnet.PlantSheetDestination
import org.druidanet.druidnet.data.PreferencesState
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.data.plant.PlantsRepository

private const val TIMEOUT_MILLIS = 5_000L

class PlantSheetViewModel (
    savedStatedHandle: SavedStateHandle,
    plantsRepository: PlantsRepository,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val plantLatinName: String = checkNotNull(savedStatedHandle[PlantSheetDestination.plantArg])

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

    val uiState: StateFlow<PlantSheetUIState> = plantsRepository.getPlant(plantLatinName, language)
        .map {
            PlantSheetUIState(
                plantUiState = it,
                plantHasConfusions = it.confusions.isNotEmpty(),
                displayName = it.displayName
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PlantSheetUIState()
        )

}