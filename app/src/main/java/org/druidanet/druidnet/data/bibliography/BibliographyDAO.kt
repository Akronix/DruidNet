package org.druidanet.druidnet.data.bibliography

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BibliographyDAO {
    @Query("SELECT *" +
            "FROM Bibliography ")
    fun getAllBibliographyEntries(): Flow<List<BibliographyEntity>>

}