package org.druidanet.druidnet.network

import kotlinx.serialization.Serializable

@Serializable
data class DataBaseUpdateInfo (
    val versionDB: Long,
    val images: List<String>
)