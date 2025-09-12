package org.druidanet.druidnet.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerializationException
import org.druidanet.druidnet.data.AppDatabase
import org.druidanet.druidnet.data.DocumentsRepository
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.data.bibliography.BibliographyDAO
import org.druidanet.druidnet.data.bibliography.BibliographyRepository
import org.druidanet.druidnet.data.images.ImagesRepository
import org.druidanet.druidnet.data.plant.PlantDAO
import org.druidanet.druidnet.data.plant.PlantsRepository
import org.druidanet.druidnet.network.BackendApiService
import java.io.IOException
import java.net.UnknownHostException

@HiltWorker
class DatabaseUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val plantsRepository: PlantsRepository,
    private val biblioRepository: BibliographyRepository,
    private val imagesRepository: ImagesRepository,
    private val documentsRepository: DocumentsRepository,
    private val appDatabase: AppDatabase,
    private val plantDao: PlantDAO,
    private val biblioDao: BibliographyDAO,
    private val backendApiService: BackendApiService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.i("DruidNetWorker", "DatabaseUpdateWorker started.")
            val currentDBVersion: Long = userPreferencesRepository.getDatabaseVersion.first()
            val res = backendApiService.getLastUpdate()
            Log.i(
                "DruidNetWorker",
                "Checking new versions of the database... Last update: ${res.versionDB}. Current version: $currentDBVersion"
            )

            if (res.versionDB > currentDBVersion) {
                Log.i("DruidNetWorker", "New database version found (${res.versionDB}). Current version ($currentDBVersion). Starting update...")

                // 1. Download all plants and bibliography entries
                val plantData = plantsRepository.fetchPlantData()
                val biblioData = biblioRepository.getBiblioData()
                Log.i("DruidNetWorker", "Downloaded ${plantData.plants.size} plants and ${biblioData.size} bibliography entries.")

                // 2. Download images
                val imageList = res.images
                Log.i("DruidNetWorker", "Downloading ${imageList.size} images.")
                imagesRepository.fetchImages(imageList)
                Log.i("DruidNetWorker", "Image download process completed.")

                // 3. Download credits.md
                documentsRepository.downloadCreditsMd()
                Log.i("DruidNetWorker", "Downloaded credits.md.")

                // 4. Update database in a transaction
                appDatabase.withTransaction {
                    Log.i("DruidNetWorker", "Starting database transaction: clearing old data and populating new data.")
                    appDatabase.clearAllTables() // Assuming this method exists and clears all relevant tables
                    Log.i("DruidNetWorker", "Cleared all tables from database.")

                    plantDao.populatePlants(plantData.plants)
                    plantDao.populateConfusions(plantData.confusions)
                    plantDao.populateNames(plantData.names)
                    plantDao.populateUsages(plantData.usages)
                    biblioDao.populateData(biblioData)
                    Log.i("DruidNetWorker", "Populated database with new data.")
                }
                userPreferencesRepository.updateDatabaseVersion(res.versionDB)
                Log.i("DruidNetWorker", "Database updated successfully to version ${res.versionDB}!")
            } else {
                Log.i("DruidNetWorker", "Database is already up to date (version $currentDBVersion).")
            }

            // Update recommendations if a new version is available
            if (res.versionRecommendations > userPreferencesRepository.getRecommendationsVersion.first()) {
                Log.i("DruidNetWorker", "New recommendations version found (${res.versionRecommendations}). Downloading...")
                if (documentsRepository.downloadRecommendationsMd()) {
                    userPreferencesRepository.updateVersion(
                        "recommendations",
                        res.versionRecommendations
                    )
                    Log.i("DruidNetWorker", "Recommendations updated to version ${res.versionRecommendations}.")
                } else {
                    Log.w("DruidNetWorker", "Failed to download recommendations.")
                }
            }

            // Update glossary if a new version is available
            if (res.versionGlossary > userPreferencesRepository.getGlossaryVersion.first()) {
                Log.i("DruidNetWorker", "New glossary version found (${res.versionGlossary}). Downloading...")
                if (documentsRepository.downloadGlossaryMd()) {
                    userPreferencesRepository.updateVersion(
                        "glossary",
                        res.versionGlossary
                    )
                    Log.i("DruidNetWorker", "Glossary updated to version ${res.versionGlossary}.")
                } else {
                    Log.w("DruidNetWorker", "Failed to download glossary.")
                }
            }
            Log.i("DruidNetWorker", "DatabaseUpdateWorker finished successfully.")
            Result.success()
        } catch (e: SerializationException) {
            Log.e("DruidNetWorker", "Serialization Error during database update: ${e.message}", e)
            Result.failure()
        } catch (e: UnknownHostException) {
            Log.e("DruidNetWorker", "No internet connection during database update: ${e.message}", e)
            Result.retry() // Retry if there's no network
        } catch (e: IOException) {
            Log.e("DruidNetWorker", "IO Error during database update: ${e.message}", e)
            Result.failure()
        } catch (e: Exception) { // Catch any other unexpected exceptions
            Log.e("DruidNetWorker", "Unexpected error during database update: ${e.message}", e)
            Result.failure()
        }
    }
}
