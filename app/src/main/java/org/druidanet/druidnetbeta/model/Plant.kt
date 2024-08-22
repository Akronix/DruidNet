package org.druidanet.druidnetbeta.model

import androidx.annotation.DrawableRes

data class Plant(
    val latinName: String,

    val commonNames: Array<Name>,
    val displayName: String = commonNames[0].name,

    val usages: Map<UsageType, Usage>,
    val family: String,
    val toxic: Boolean = false,
    val description: String,
    val habitat: String,
    val phenology: String,
    val distribution: String,
    val confusions: Array<Confusion>,
    val observations: String? = null,

    @DrawableRes val imageResourceId: Int

    // otherImages
)

data class Name (
    val name: String,
    val language: LanguageEnum
)

class Confusion (
    val latinName: String,
    val text: String
)

data class Usage (
    val type: UsageType,
    val text: String
)
