package org.druidanet.druidnet.data.plant

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.model.PlantCard
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
                val displayName = plantDao.getDisplayName(it.p.plantId, language).firstOrNull() ?: plantLatinName
                it.toPlant(displayName = displayName)
            }
    }

    fun searchPlantsByName(name: String, originalListPlants: Flow<List<PlantCard>>) : Flow<List<PlantCard>> {
        val plantsMatchingName : Flow<List<Int>> = plantDao.getPlantsWithName(name)
        return plantsMatchingName.combine( originalListPlants, { plantIds, plants ->
            plants.filter {
                plant -> plant.plantId in plantIds
            }
        }
        )

    }

    suspend fun searchPlant(speciesName: String, language: LanguageEnum): Plant? {
        // First, try to find the exact species
        val plantData = plantDao.getPlant(speciesName).firstOrNull()

        if (plantData != null) {
            // Found the exact species, get display name and convert to Plant object
            val displayName = plantDao.getDisplayName(plantData.p.plantId, language).firstOrNull() ?: speciesName
            return plantData.toPlant(displayName = displayName)
        }

        // If not found, try to find the genus (e.g., "Quercus spp.")
        val genusName = speciesName.split(" ").first() + " spp."
        val genusData = plantDao.getPlant(genusName).firstOrNull()

        if (genusData != null) {
            // Found the genus, get display name and convert to Plant object
            val displayName = plantDao.getDisplayName(genusData.p.plantId, language).firstOrNull() ?: genusName
            return genusData.toPlant(displayName = displayName)
        }

        // If neither was found, return null
        return null
    }

}