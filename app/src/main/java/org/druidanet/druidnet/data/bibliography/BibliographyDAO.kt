package org.druidanet.druidnet.data.bibliography

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.druidanet.druidnet.data.plant.PlantData

@Dao
interface BibliographyDAO {
    @Query("SELECT *" +
            "FROM Bibliography ")
    fun getAllBibliographyEntries(): Flow<List<BibliographyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun populateData(plants: List<BibliographyEntity>)

}