package org.druidanet.druidnet.data

import org.druidanet.druidnet.data.plant.PlantsRemoteDataSource
import org.druidanet.druidnet.network.PlantDataDTO

class PlantsRepository(
    private val plantsRemoteDataSource: PlantsRemoteDataSource
) {
    suspend fun fetchPlantData() : PlantDataDTO =
        plantsRemoteDataSource.downloadPlantData()

}