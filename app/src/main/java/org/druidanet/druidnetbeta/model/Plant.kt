package org.druidanet.druidnetbeta.model

import androidx.annotation.DrawableRes

interface PlantBase {
    val plantId: Int

//    val latinName: String,

    val displayName: String

    val imageResourceId: Int
}

data class PlantBasic(
    override val plantId: Int,
    override val displayName: String,
    override val imageResourceId: Int
) : PlantBase


data class Plant (
    val plantId: Int,

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

data class Confusion (
    val latinName: String,
    val text: String
)

data class Usage (
    val type: UsageType,
    val text: String
)
