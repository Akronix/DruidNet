package org.druidanet.druidnetbeta.ui

import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.druidanet.druidnetbeta.DruidNetApplication
import org.druidanet.druidnetbeta.data.DruidNetUiState
import org.druidanet.druidnetbeta.data.PlantDAO
import org.druidanet.druidnetbeta.data.PlantView
import org.druidanet.druidnetbeta.data.PlantsDataSource
import org.druidanet.druidnetbeta.model.LanguageEnum
import org.druidanet.druidnetbeta.model.Plant
import org.druidanet.druidnetbeta.model.PlantBasic

class DruidNetViewModel(private val plantDao: PlantDAO) : ViewModel(){
    private val _uiState = MutableStateFlow(DruidNetUiState())
    val uiState: StateFlow<DruidNetUiState> = _uiState.asStateFlow()

    /**
     * Set the current [selectPlant] to show information of
     */
    fun setSelectedPlant(selectPlant: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedPlant = selectPlant)
        }

    }

    // Get all plants from db
    fun getAllPlants() =
        plantDao.getPlantCatalogData(LanguageEnum.CASTELLANO)
                .map { list ->
                    list.map { it.toPlantBasic() }
                }



    // Get specific plant from db
//    fun getPlant(plantId: Int): Flow<PlantEntity> =
//        plantDao.getPlant(plantId)
    fun getPlant(plantId: Int): Plant {
        return PlantsDataSource.loadPlants()[plantId-1]
    }

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DruidNetApplication)
                DruidNetViewModel( application.database.plantDao())
            }
        }
    }
}

private fun PlantView.toPlantBasic(): PlantBasic =
    PlantBasic(
        plantId = plantId,
        displayName = common_name,
        imageResourceId = image
    )
