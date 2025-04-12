package org.druidanet.druidnet.data.plant

import org.druidanet.druidnet.network.BackendApi

class PlantsRemoteDataSource() {
    suspend fun downloadPlantData() = BackendApi.retrofitService.downloadPlantData()
}