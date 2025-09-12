package org.druidanet.druidnet.data.plant

import org.druidanet.druidnet.network.BackendApiService
import javax.inject.Inject

class PlantsRemoteDataSource @Inject constructor(private val backendApiService: BackendApiService) {
    suspend fun downloadPlantData() = backendApiService.downloadPlantData()
}