package org.druidanet.druidnet.network

import kotlinx.serialization.Serializable

// Using @Serializable for potential use with Kotlinx Serialization, which is common.
// If you use Gson, these annotations are not strictly needed by Gson itself but don't hurt.

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
    val images: List<String>? = null, // These are image identifiers, not full image objects
    val organs: List<String>? = null,
    val includeRelatedImages: Boolean? = null,
    val noReject: Boolean? = null,
    val type: String? = null // This was null in the JSON, so it's nullable
)

@Serializable
data class PredictedOrgan(
    val image: String? = null, // Image identifier
    val filename: String? = null,
    val organ: String? = null,
    val score: Double? = null
)

@Serializable
data class PlantResult(
    val score: Double? = null,
    val species: SpeciesInfo? = null,
    val images: List<PlantImage>? = null, // Added this line to include images list
    val gbif: GbifInfo? = null,
    val powo: PowoInfo? = null
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
data class PlantImage(
    val organ: String? = null,
    val author: String? = null,
    val license: String? = null,
    val date: DateInfo? = null,
    val url: ImageUrls? = null,
    val citation: String? = null
)

@Serializable
data class DateInfo(
    val timestamp: Long? = null,
    val string: String? = null
)

@Serializable
data class ImageUrls(
    val o: String? = null, // Original size URL
    val m: String? = null, // Medium size URL
    val s: String? = null  // Small size URL
)

@Serializable
data class GbifInfo(
    val id: String? = null
)

@Serializable
data class PowoInfo(
    val id: String? = null
)
