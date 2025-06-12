package org.druidanet.druidnet.data

import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.ui.plant_sheet.DEFAULT_SECTION
import org.druidanet.druidnet.ui.plant_sheet.PlantSheetSection
import org.druidanet.druidnet.utils.DEFAULT_CREDITS_TXT

data class DruidNetUiState(

    val firstLaunch: Boolean = true,

    val creditsTxt: String = DEFAULT_CREDITS_TXT,
)

data class PreferencesState(
    val displayLanguage: LanguageEnum = LanguageEnum.CASTELLANO
)
