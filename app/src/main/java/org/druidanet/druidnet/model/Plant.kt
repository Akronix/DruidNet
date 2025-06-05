package org.druidanet.druidnet.model

interface PlantBase {
    val plantId: Int

    val displayName: String

    val imagePath: String

    val latinName: String

}

open class PlantBasic(
    override val plantId: Int,
    override val displayName: String,
    override val imagePath: String,
    override val latinName: String
) : PlantBase


data class PlantCard(
    override val plantId: Int,
    override val displayName: String,
    override val imagePath: String,
    override val latinName: String,
    val isLatinName: Boolean
) : PlantBasic(plantId, displayName, imagePath, latinName)


data class Plant (
    override val plantId: Int,

    override val latinName: String,

    val commonNames: Array<Name>,
    override val displayName: String = latinName,

    val usages: Map<UsageType, List<Usage>>,
    val family: String,

    val toxic: Boolean = false,
    val toxic_text: String? = null,

    val description: String,
    val habitat: String,
    val phenology: String,
    val distribution: String,
    val confusions: Array<Confusion>,

    val observations: String? = null,
    val curiosities: String? = null,

    override val imagePath: String,

    // otherImages
): PlantBasic(plantId, displayName, latinName, imagePath)

data class Name (
    val name: String,
    val language: LanguageEnum
)

data class Confusion (
    val latinName: String,
    val text: String,
    val imagePath: String? = null,
    val captionText: String? = null
)

data class Usage (
    val type: UsageType,
    val subType: String,
    val text: String
)
