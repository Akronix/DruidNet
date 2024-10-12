package org.druidanet.druidnetbeta.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.druidanet.druidnetbeta.DruidNetApplication
import org.druidanet.druidnetbeta.LANGUAGE_APP
import org.druidanet.druidnetbeta.data.DruidNetUiState
import org.druidanet.druidnetbeta.data.PlantDAO
import org.druidanet.druidnetbeta.data.PlantData
import org.druidanet.druidnetbeta.data.PlantEntity
import org.druidanet.druidnetbeta.data.PlantView
import org.druidanet.druidnetbeta.data.UserPreferencesRepository
import org.druidanet.druidnetbeta.model.Confusion
import org.druidanet.druidnetbeta.model.LanguageEnum
import org.druidanet.druidnetbeta.model.Name
import org.druidanet.druidnetbeta.model.Plant
import org.druidanet.druidnetbeta.model.PlantBasic
import org.druidanet.druidnetbeta.model.Usage
import org.druidanet.druidnetbeta.model.UsageType

class DruidNetViewModel(
    private val plantDao: PlantDAO,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(DruidNetUiState())
    val uiState: StateFlow<DruidNetUiState> = _uiState.asStateFlow()

    /****** VIEW MODEL CONSTRUCTOR *****/

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DruidNetApplication)
                DruidNetViewModel(
                    application.database.plantDao(),
                    application.userPreferencesRepository
                )
            }
        }
    }

    /****** USER INTERACTION (UI) FUNCTIONS *****/

    /**
     * Set the current [selectPlant] to show information of
     */
    fun setSelectedPlant(selectPlant: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedPlant = selectPlant)
        }

    }

    fun changeSection(newSection: PlantSheetSection) {
        if (newSection != uiState.value.currentSection )
            _uiState.update { currentState ->
                currentState.copy(currentSection = newSection)
            }
    }

    /****** DATABASE FUNCTIONS *****/

    /**
     * Update the item in the [ItemsRepository]'s data source
     */
    suspend fun updatePlantUi(selectPlant: Int) {
        _uiState.update { currentState ->
            currentState.copy(plantUiState =
                this.getPlant(selectPlant)
                    .filterNotNull()
                    .first()
                    .toPlant(displayName =
                        plantDao.getDisplayName(selectPlant, LANGUAGE_APP)
                            .first(),
                    )
                )
        }
    }

    // Get all plants from db
    fun getAllPlants() =
        plantDao.getPlantCatalogData(LANGUAGE_APP)
                .map { list ->
                    list.map { it.toPlantBasic() }
                }

    /**
     * Retrieve a specific plant from the given data source that matches with the [plantId].
     */
    fun getPlant(plantId: Int): Flow<PlantData?> =
        plantDao.getPlant(plantId)


    /****** USER PREFERENCES FUNCTIONS *****/

    fun unsetFirstLaunch() =
        viewModelScope.launch {
            userPreferencesRepository.unsetFirstLaunch()
        }

    fun isFirstLaunch() =
        userPreferencesRepository.isFirstLaunch

}

/****** OTHERS - HELPER FUNCTIONS *****/

fun PlantView.toPlantBasic(): PlantBasic =
    PlantBasic(
        plantId = plantId,
        displayName = common_name,
        imagePath = image_path
    )

fun PlantData.toPlant(displayName: String): Plant =
    Plant(
        plantId = p.plantId,
        latinName = p.latinName,

        commonNames = names.map {Name(it.commonName, it.language)}.toTypedArray(),

        displayName = displayName,

        usages = usages
            .map{ Usage( it.type, it.text ) }
            .groupBy { it.type },
        family = p.family,

        toxic = p.toxic,
        toxic_text = p.toxicText,

        description = p.description,
        habitat = p.habitat,
        phenology = p.phenology,
        distribution = p.distribution,
        confusions = confusions.map {
            Confusion(it.latinName, it.text, it.imagePath, it.captionText)
        }.toTypedArray(),
        observations = p.observations,
        curiosities = p.curiosities,

        imagePath = p.imagePath
    )
