package org.druidanet.druidnet.ui

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import org.druidanet.druidnet.DruidNetApplication
import org.druidanet.druidnet.data.DocumentsRepository
import org.druidanet.druidnet.data.DruidNetUiState
import org.druidanet.druidnet.data.PreferencesState
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.data.bibliography.BibliographyDAO
import org.druidanet.druidnet.data.bibliography.BibliographyEntity
import org.druidanet.druidnet.data.bibliography.BibliographyRepository
import org.druidanet.druidnet.data.images.ImagesRepository
import org.druidanet.druidnet.data.plant.PlantDAO
import org.druidanet.druidnet.data.plant.PlantData
import org.druidanet.druidnet.data.plant.PlantsRepository
import org.druidanet.druidnet.model.Confusion
import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.model.Name
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.model.PlantCard
import org.druidanet.druidnet.model.Usage
import org.druidanet.druidnet.network.BackendApi
import org.druidanet.druidnet.network.BackendScalarApi
import org.druidanet.druidnet.utils.mergeOrderedLists
import java.io.IOException
import java.text.Collator
import java.util.Locale

class DruidNetViewModel(
    private val plantDao: PlantDAO,
    private val biblioDao: BibliographyDAO,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val plantsRepository: PlantsRepository,
    private val biblioRepository: BibliographyRepository,
    private val imagesRepository: ImagesRepository,
    private val documentsRepository: DocumentsRepository,
    private val roomDatabase: RoomDatabase,
    private val assets: AssetManager
) : ViewModel() {

    /****** STATE VARIABLES *****/

    private val _uiState = MutableStateFlow(DruidNetUiState())
    val uiState: StateFlow<DruidNetUiState> = _uiState.asStateFlow()

    private val preferencesState: StateFlow<PreferencesState> =
        userPreferencesRepository.getDisplayNameLanguagePreference.map { displayLanguage ->
            PreferencesState(displayLanguage = displayLanguage)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = runBlocking {
                    PreferencesState(
                        displayLanguage = userPreferencesRepository.getDisplayNameLanguagePreference.first()
                    )
                }
            )

    var LANGUAGE_APP = preferencesState.value.displayLanguage

    /****** VIEW MODEL CONSTRUCTOR *****/

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DruidNetApplication)
                DruidNetViewModel(
                    application.database.plantDao(),
                    application.database.biblioDao(),
                    application.userPreferencesRepository,
                    application.plantsRepository,
                    application.biblioRepository,
                    application.imagesRepository,
                    application.documentsRepository,
                    application.database,
                    application.assets
                )
            }
        }
    }

    /****** USER INTERACTION (UI) FUNCTIONS *****/

    /****** NETWORK FUNCTIONS *****/
    fun checkAndUpdateDatabase(snackbarHost: SnackbarHostState) {
        viewModelScope.launch {
            try {
                /* TO RETHINK ALL THIS CODE */
                val currentDBVersion: Long = userPreferencesRepository.getDatabaseVersion.first()
                val res = BackendApi.retrofitService.getLastUpdate()
                Log.i(
                    "DruidNet",
                    "Checking new versions of the database...Last update: ${res.versionDB}. Current version: $currentDBVersion"
                )

                // If current version older than new update version:
                if (res.versionDB > currentDBVersion) {

                    snackbarHost.showSnackbar("Descargando actualización de la base de datos...")

                    //  1. Download all plants and bibliography entries
                    val data = plantsRepository.fetchPlantData()
                    val biblio = biblioRepository.getBiblioData()
                    Log.i("DruidNet", "Downloaded ${biblio.size} bibliography entries")

                    //  2. Download images
                    val imageList = res.images
                    Log.i("DruidNet", "Downloading images:\n $imageList")
                    imagesRepository.fetchImages(imageList)

                    // 3. Download credits.md
                    documentsRepository.downloadCreditsMd()

                    // (The next two steps, ideally, would be done in one atomic transaction)
//                 withContext(Dispatchers.IO) {
                    roomDatabase.withTransaction {

                        //  3. Delete all data in localdb
                        clearDB()
                        //  4. Substitute new data in localdb
                        plantDao.populatePlants(data.plants)
                        plantDao.populateConfusions(data.confusions)
                        plantDao.populateNames(data.names)
                        plantDao.populateUsages(data.usages)
                        biblioDao.populateData(biblio)
                    }
                    userPreferencesRepository.updateDatabaseVersion(res.versionDB)
                    snackbarHost.showSnackbar("¡Base de datos actualizada con éxito!")
                } else {
                    Log.i("DruidNet", "La base de datos está al día.")
                }

                if (res.versionRecommendations > userPreferencesRepository.getRecommendationsVersion.first()) {
                    Log.i("DruidNet", "Downloading recommendations...")
                    snackbarHost.showSnackbar("Actualizando texto de recomendaciones...")
                    if (documentsRepository.downloadRecommendationsMd()) {
                        userPreferencesRepository.updateVersion(
                            "recommendations",
                            res.versionRecommendations)
                        Log.i("DruidNet", "Recommendations updated!")
                    }

                }
                if (res.versionGlossary > userPreferencesRepository.getGlossaryVersion.first()) {
                    Log.i("DruidNet", "Downloading glossary...")
                    snackbarHost.showSnackbar("Actualizando glosario...")
                    if (documentsRepository.downloadGlossaryMd()) {
                        userPreferencesRepository.updateVersion(
                            "glossary",
                            res.versionGlossary
                        )
                        Log.i("DruidNet", "Glossary updated!")
                    }

                }


            } catch (e: SerializationException) {
                Log.e("DruidNet", "Serialization Error: ${e.message}", e)
                snackbarHost.showSnackbar("Error procesando los datos de descarga")
            } catch (e: java.net.UnknownHostException) {
                Log.e("DruidNet", "No internet connection.", e)
            } catch (e: IOException) {
                Log.e("DruidNet", "IO Error: ${e.message}", e)
                snackbarHost.showSnackbar("Error actualizando la base de datos")
            }
        }
    }


    /****** DATABASE FUNCTIONS *****/

    // Get all plants from db
    fun getAllPlants(): Flow<List<PlantCard>> =
        if (LANGUAGE_APP != LanguageEnum.LATIN) {
            combine(
                plantDao.getPlantCatalogData(LANGUAGE_APP),
                plantDao.getPlantCatalogLatinNotInLanguage(LANGUAGE_APP)
            )
            { plantsInLanguage, plantsInLatin ->
                if (plantsInLatin.isEmpty())
                    plantsInLanguage.map {
                        PlantCard(
                            it.plantId,
                            it.displayName,
                            it.imagePath,
                            it.latinName,
                            false
                        )
                    }
                else
                    mergeOrderedLists(
                        plantsInLanguage.map {
                            PlantCard(
                                it.plantId,
                                it.displayName,
                                it.imagePath,
                                it.latinName,
                                false
                            )
                        },
                        plantsInLatin.map {
                            PlantCard(
                                it.plantId,
                                it.displayName,
                                it.imagePath,
                                it.latinName,
                                true
                            )
                        },
                        compareBy = compareBy(
                            Collator.getInstance(Locale("es", "ES"))
                        ) { it.displayName }
                    )
            }

        } else {
            plantDao.getPlantCatalogLatin().map { list ->
                list.map { plant ->
                    PlantCard(
                        plant.plantId,
                        plant.displayName,
                        plant.imagePath,
                        plant.latinName,
                        true
                    )
                }
            }
        }

    /**
     * Retrieve a specific plant from the given data source that matches with the [plantId].
     */
    fun getPlant(plantId: Int): Flow<PlantData?> =
        plantDao.getPlant(plantId)

    fun getBibliography(): Flow<List<BibliographyEntity>> =
        biblioDao.getAllBibliographyEntries()

    fun clearDB() = roomDatabase.clearAllTables()


    /****** USER PREFERENCES FUNCTIONS *****/

    fun unsetFirstLaunch() =
        viewModelScope.launch {
            userPreferencesRepository.unsetFirstLaunch()
        }

    fun isFirstLaunch() =
        userPreferencesRepository.isFirstLaunch

    fun setLanguage(language: LanguageEnum) {
        viewModelScope.launch {
            userPreferencesRepository.updateDisplayNameLanguagePreference(language)
            LANGUAGE_APP = language
        }

    }

    fun getDisplayNameLanguage(): LanguageEnum = LANGUAGE_APP /* It should use userPreferencesRepository.getDisplayNameLanguagePreference ??
                                                                    but then we have to query this value using a flow coroutine */
    fun getCreditsText(): String =
        documentsRepository.getCreditsMd()

    fun getRecommendationsText(): String =
        documentsRepository.getRecommendationsMd()

    fun getGlossaryText(): String =
        documentsRepository.getGlossaryMd()

//    fun getAssetsImage(): ImageBitmap {
//        val inputStream = assets.open("drawable/gatherer_basket.webp")
//        val bitmap = BitmapFactory.decodeStream(inputStream)
//        inputStream.close()
//        return bitmap.asImageBitmap()
//    }

//    suspend fun setDisplayName(plantLatinName: String): String {
//        displayName = plantDao.getDisplayName(plantLatinName)
//        updateDisplayName(displayName)
//        return displayName
//    }

}
    /****** OTHERS - HELPER FUNCTIONS *****/

    fun PlantData.toPlant(displayName: String): Plant =
        Plant(
            plantId = p.plantId,
            latinName = p.latinName,

            commonNames = names.map { Name(it.commonName, it.language) }.toTypedArray(),

            displayName = displayName,

            usages = usages
                .map { Usage(it.type, it.subType, it.text) }
                .groupBy { it.type },
            family = p.family,

            toxic = p.toxic,
            toxic_text = p.toxicText,

            description = p.description,
            habitat = p.habitat,
            phenology = p.phenology,
            distribution = p.distribution,
            confusions = confusions.map {
                Confusion(it.latinName, it.text, it.imagePath, it.captionText)
            }.toTypedArray(),
            observations = p.observations,
            curiosities = p.curiosities,

            imagePath = p.imagePath
        )