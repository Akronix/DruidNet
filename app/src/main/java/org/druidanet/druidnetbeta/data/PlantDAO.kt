package org.druidanet.druidnetbeta.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.druidanet.druidnetbeta.model.LanguageEnum

/**
 * Database access object to access the Plant in the DruidNet database
 */
@Dao
interface PlantDAO {

    @Query("SELECT * from PlantView WHERE language = :language")
    fun getPlantCatalogData(language: LanguageEnum): Flow<List<PlantView>>


    @Query("SELECT * from Plant WHERE plantId = :plantId")
    fun getPlant(plantId: Int): Flow<PlantEntity>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun populateData(plants: List<PlantEntity>)


}
