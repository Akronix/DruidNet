package org.druidanet.druidnet.data

import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.ui.screens.DEFAULT_SECTION
import org.druidanet.druidnet.ui.screens.PlantSheetSection
import org.druidanet.druidnet.utils.DEFAULT_CREDITS_TXT

data class DruidNetUiState(
    /** Selected plant to show */
    val selectedPlant: Int = 0,

    /**
     * Holds current plant ui state
     */
    val plantUiState: Plant? = null,

    val plantHasConfusions: Boolean = false,

    val currentSection: PlantSheetSection = DEFAULT_SECTION,

    val firstLaunch: Boolean = true,

    val creditsTxt: String = DEFAULT_CREDITS_TXT,
)

data class PreferencesState(
    val displayLanguage: LanguageEnum = LanguageEnum.CASTELLANO
)
