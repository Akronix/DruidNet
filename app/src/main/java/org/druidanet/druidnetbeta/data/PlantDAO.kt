package org.druidanet.druidnetbeta.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.druidanet.druidnetbeta.model.LanguageEnum

/**
 * Database access object to access the Plant in the DruidNet database
 */
@Dao
interface PlantDAO {

    @Query("SELECT * FROM PlantView WHERE language = :language")
    fun getPlantCatalogData(language: LanguageEnum): Flow<List<PlantView>>

    @Query("SELECT common_name as displayName " +
            "FROM PlantView" +
            " WHERE language = :language" +
            " AND plantId=:plantId")
    fun getDisplayName(plantId: Int, language: LanguageEnum): Flow<String>


    @Query("SELECT * FROM Plant WHERE plantId = :plantId")
    @Transaction
    fun getPlant(plantId: Int): Flow<PlantData>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    fun populateData(plants: List<PlantData>)


}
