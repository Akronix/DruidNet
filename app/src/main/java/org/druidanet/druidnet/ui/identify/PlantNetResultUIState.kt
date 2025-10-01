package org.druidanet.druidnet.ui.identify

import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.network.PlantResult

data class PlantNetResultUIState (
    /**
     * Holds current plant ui state
     */
    val plant: Plant? = null,

    val isInDatabase: Boolean = false,

    val latinName: String = "",

    val score: Double = 0.0,

    val similarPlants: List<PlantResult> = emptyList<PlantResult>(),
)