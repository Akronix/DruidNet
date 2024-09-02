package org.druidanet.druidnetbeta.data

import org.druidanet.druidnetbeta.model.Plant

data class DruidNetUiState(
    /** Selected plant to show */
    val selectedPlant: Int = 0,

    /**
     * Holds current plant ui state
     */
    var plantUiState: Plant? = null

)
