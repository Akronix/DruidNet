package org.druidanet.druidnet

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.druidanet.druidnet.data.AppDatabase
import org.druidanet.druidnet.data.BibliographyRepository
import org.druidanet.druidnet.data.PlantsRepository
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.data.images.ImagesLocalDataSource
import org.druidanet.druidnet.data.images.ImagesRepository
import org.druidanet.druidnet.data.plant.PlantsRemoteDataSource


private const val USER_PREFERENCES_NAME = "preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class DruidNetApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(dataStore) }
    val plantsRepository by lazy { PlantsRepository(PlantsRemoteDataSource()) }
    val biblioRepository by lazy { BibliographyRepository() }
    val imagesRepository by lazy {
        ImagesRepository(
            ImagesLocalDataSource(this.applicationContext.filesDir.absolutePath)
        )
    }

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }

}