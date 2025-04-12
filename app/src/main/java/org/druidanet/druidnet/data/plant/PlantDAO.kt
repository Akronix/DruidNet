package org.druidanet.druidnet.data.plant

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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


    @Query("SELECT plantId, latin_name as displayName, image_path as imagePath" +
            " FROM PlantView " +
            " EXCEPT" +
            " SELECT plantId, latin_name as displayName, image_path as imagePath " +
            " FROM PlantView" +
            " WHERE language = :language" +
            " ORDER BY displayName")
    fun getPlantCatalogLatinNotInLanguage(language: LanguageEnum): Flow<List<PlantBasic>>


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

    /*** INSERT DATA ***/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun populatePlants(plants: List<PlantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun populateNames(names: List<NameEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun populateConfusions(names: List<ConfusionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun populateUsages(names: List<UsageEntity>)

}