package org.druidanet.druidnet.network

import kotlinx.serialization.Serializable

@Serializable
data class DataBaseUpdateInfo (
    val versionDB: Long,
    val versionGlossary: Long,
    val versionRecommendations: Long,
    val images: List<String>
)