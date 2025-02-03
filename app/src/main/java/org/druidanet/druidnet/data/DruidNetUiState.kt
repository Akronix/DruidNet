package org.druidanet.druidnet.data

import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.ui.DEFAULT_SECTION
import org.druidanet.druidnet.ui.PlantSheetSection

data class DruidNetUiState(
    /** Selected plant to show */
    val selectedPlant: Int = 0,

    /**
     * Holds current plant ui state
     */
    val plantUiState: Plant? = null,

    val plantHasConfusions: Boolean = false,

    val currentSection: PlantSheetSection = DEFAULT_SECTION,

    val firstLaunch: Boolean = true
)

data class PreferencesState(
    val displayLanguage: LanguageEnum = LanguageEnum.CASTELLANO
)
