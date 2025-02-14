package org.druidanet.druidnet.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.model.PlantBasic

/**
 * Database Access Object to access the Plant Entities in the DruidNet database
 */
@Dao
interface PlantDAO {

    @Query("SELECT DISTINCT common_name as displayName, plantId, image_path as imagePath " +
            "FROM PlantView " +
            "WHERE language = :language " +
            "ORDER BY displayName " +
            "COLLATE LOCALIZED")
    fun getPlantCatalogData(language: LanguageEnum): Flow<List<PlantBasic>>


    @Query("SELECT DISTINCT latin_name as displayName, plantId, image_path as imagePath" +
            " FROM PlantView" +
            " ORDER BY latin_name")
    fun getPlantCatalogLatin(): Flow<List<PlantBasic>>

    @Query("SELECT common_name as displayName" +
            " FROM PlantView" +
            " WHERE language = :language" +
            " AND plantId=:plantId")
    fun getDisplayName(plantId: Int, language: LanguageEnum): Flow<String>

    @Query("SELECT latin_name" +
            " FROM PlantView" +
            " WHERE plantId=:plantId")
    fun getLatinName(plantId: Int): Flow<String>

    @Query("SELECT * FROM Plant WHERE plantId = :plantId")
    @Transaction
    fun getPlant(plantId: Int): Flow<PlantData>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    fun populateData(plants: List<PlantData>)

}
