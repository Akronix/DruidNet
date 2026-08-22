package org.druidanet.druidnet.data.bibliography

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BibliographyDAO {
    @Query("SELECT *" +
            "FROM Bibliography ")
    fun getAllBibliographyEntries(): Flow<List<BibliographyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun populateData(plants: List<BibliographyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun populatePlantReferences(biblioReferences: List<PlantReferenceEntity>)

    @Query("SELECT b.* FROM Bibliography b INNER JOIN Plant_References pr ON b.refId = pr.refId WHERE pr.plantId = :plantId")
    fun getBibliographyForPlant(plantId: Int): Flow<List<BibliographyEntity>>

}