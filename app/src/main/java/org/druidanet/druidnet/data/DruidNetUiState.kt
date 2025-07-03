package org.druidanet.druidnet.data

import org.druidanet.druidnet.model.LanguageEnum

data class DruidNetUiState(

    val firstLaunch: Boolean = true,

)

data class PreferencesState(
    val displayLanguage: LanguageEnum = LanguageEnum.CASTELLANO
)
