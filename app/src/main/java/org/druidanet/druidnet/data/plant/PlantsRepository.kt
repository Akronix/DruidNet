package org.druidanet.druidnet.data.plant

import org.druidanet.druidnet.network.PlantDataDTO

class PlantsRepository(
    private val plantsRemoteDataSource: PlantsRemoteDataSource
) {
    suspend fun fetchPlantData() : PlantDataDTO =
        plantsRemoteDataSource.downloadPlantData()

}