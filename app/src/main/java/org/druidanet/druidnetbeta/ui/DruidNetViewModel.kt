package org.druidanet.druidnetbeta.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.druidanet.druidnetbeta.data.DruidNetUiState
import org.druidanet.druidnetbeta.model.Plant

class DruidNetViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(DruidNetUiState())
    val uiState: StateFlow<DruidNetUiState> = _uiState.asStateFlow()

    /**
     * Set the current [selectPlant] to show information of
     */
    fun setSelectedPlant(selectPlant: Plant) {
        _uiState.update { currentState ->
            currentState.copy(selectedPlant = selectPlant)
        }

    }
}