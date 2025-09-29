package org.druidanet.druidnet.network

import kotlinx.serialization.Serializable

@Serializable
data class PlantNetResponse(
    val query: QueryDetails? = null,
    val predictedOrgans: List<PredictedOrgan>? = null,
    val language: String? = null,
    val preferedReferential: String? = null,
    val bestMatch: String? = null,
    val results: List<PlantResult>? = null,
    val version: String? = null,
    val remainingIdentificationRequests: Long? = null
)

@Serializable
data class QueryDetails(
    val project: String? = null,
    val images: List<String>? = null,
    val organs: List<String>? = null,
    val includeRelatedImages: Boolean? = null,
    val noReject: Boolean? = null,
    val type: String? = null // This was null in the JSON, so it's nullable
)

@Serializable
data class PredictedOrgan(
    val image: String? = null,
    val filename: String? = null,
    val organ: String? = null,
    val score: Double? = null
)

@Serializable
data class PlantResult(
    val score: Double? = null,
    val species: SpeciesInfo? = null,
    val gbif: GbifInfo? = null,
    val powo: PowoInfo? = null
    // Note: The example JSON does not show commonNames or images directly under PlantResult,
    // they are nested within species or potentially other objects if the API provides them here.
)

@Serializable
data class SpeciesInfo(
    val scientificNameWithoutAuthor: String? = null,
    val scientificNameAuthorship: String? = null,
    val genus: GenusFamilyInfo? = null,
    val family: GenusFamilyInfo? = null,
    val commonNames: List<String>? = null,
    val scientificName: String? = null
)

@Serializable
data class GenusFamilyInfo(
    val scientificNameWithoutAuthor: String? = null,
    val scientificNameAuthorship: String? = null,
    val scientificName: String? = null
)

@Serializable
data class GbifInfo(
    val id: String? = null
)

@Serializable
data class PowoInfo(
    val id: String? = null
)
