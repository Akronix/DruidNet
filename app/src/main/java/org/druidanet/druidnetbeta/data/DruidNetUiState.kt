package org.druidanet.druidnetbeta.data

import org.druidanet.druidnetbeta.model.Plant
import org.druidanet.druidnetbeta.ui.DEFAULT_SECTION
import org.druidanet.druidnetbeta.ui.PlantSheetSection

data class DruidNetUiState(
    /** Selected plant to show */
    val selectedPlant: Int = 0,

    /**
     * Holds current plant ui state
     */
    val plantUiState: Plant? = null,

    val currentSection: PlantSheetSection = DEFAULT_SECTION,

    )
