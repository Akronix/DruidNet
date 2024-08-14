package org.druidanet.druidnetbeta.model

import androidx.annotation.DrawableRes

data class Plant(
    val plantId: Int,
    val latinName: String,
    val displayName: String,
    val commonNames: List<String>,
    @DrawableRes val imageResourceId: Int
)
