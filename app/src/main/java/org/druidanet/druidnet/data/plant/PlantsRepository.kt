package org.druidanet.druidnet.data.plant

import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.model.Plant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.druidanet.druidnet.network.PlantDataDTO
import org.druidanet.druidnet.ui.toPlant

class PlantsRepository(
    private val plantsRemoteDataSource: PlantsRemoteDataSource,
    private val plantDao: PlantDAO
) {
    suspend fun fetchPlantData() : PlantDataDTO =
        plantsRemoteDataSource.downloadPlantData()

    fun isPlantInDatabase(plantLatinName: String) : Flow<Boolean> {
        return plantDao.getPlant(plantLatinName)
            .map { it != null }
    }


    fun getPlant(plantLatinName: String, language: LanguageEnum) : Flow<Plant> {
        return plantDao.getPlant(plantLatinName)
            .filterNotNull()
            .map {
                val displayName = plantDao.getDisplayName(it.p.plantId, language).first()
                it.toPlant(displayName = displayName)
            }
    }

}