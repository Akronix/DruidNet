package org.druidanet.druidnet.network

import kotlinx.serialization.Serializable
import org.druidanet.druidnet.data.plant.ConfusionEntity
import org.druidanet.druidnet.data.plant.NameEntity
import org.druidanet.druidnet.data.plant.PlantEntity
import org.druidanet.druidnet.data.plant.UsageEntity

@Serializable
data class PlantDataDTO(
    val plants: List<PlantEntity>,
    val confusions: List<ConfusionEntity>,
    val names: List<NameEntity>,
    val usages: List<UsageEntity>
    )
