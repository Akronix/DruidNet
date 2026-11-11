package org.druidanet.druidnet.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile (
    val username: String,
    val role: String,
    val rank: String,
    val score: Int,
    val bio: String
)