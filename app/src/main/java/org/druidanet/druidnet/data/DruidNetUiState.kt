package org.druidanet.druidnet.data

import org.druidanet.druidnet.model.LanguageEnum

data class DruidNetUiState(
    val firstLaunch: Boolean = true,
    val isUserLoggedIn: Boolean = false,
)

data class PreferencesState(
    val displayLanguage: LanguageEnum = LanguageEnum.CASTELLANO
)
