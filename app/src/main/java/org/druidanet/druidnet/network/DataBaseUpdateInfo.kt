package org.druidanet.druidnet.network

import kotlinx.serialization.Serializable

@Serializable
data class DataBaseUpdateInfo (
    val versionDB: Long,
    val plantsChanged: Boolean,
    val biblioChanged: Boolean,
    val images: List<String>
)