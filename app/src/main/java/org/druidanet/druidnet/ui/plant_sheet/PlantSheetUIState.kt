package org.druidanet.druidnet.ui.plant_sheet

import org.druidanet.druidnet.model.Plant

/**
 * UI state for PlantSheetUIState
 */
data class PlantSheetUIState(

    val isBlank: Boolean = true,

    /**
     * Holds current plant ui state
     */
    val plantUiState: Plant? = null,

    val plantHasConfusions: Boolean = false,

    val displayName: String = "",

    val currentSection: PlantSheetSection = DEFAULT_SECTION
)