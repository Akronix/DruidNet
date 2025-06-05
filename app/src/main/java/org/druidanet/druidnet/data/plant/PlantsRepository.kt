package org.druidanet.druidnet.data.plant

import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.model.Plant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import org.druidanet.druidnet.network.PlantDataDTO
import org.druidanet.druidnet.ui.toPlant

class PlantsRepository(
    private val plantsRemoteDataSource: PlantsRemoteDataSource,
    private val plantDao: PlantDAO
) {
    suspend fun fetchPlantData() : PlantDataDTO =
        plantsRemoteDataSource.downloadPlantData()

    fun getPlant(plantLatinName: String, language: LanguageEnum) : Flow<Plant> {
        return plantDao.getPlant(plantLatinName)
            .filterNotNull()
            .map {
                val displayName = plantDao.getDisplayName(it.p.plantId, language).first()
                println("DISPLAY NAME: $displayName")
                it.toPlant(displayName = displayName)
            }
    }

}