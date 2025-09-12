package org.druidanet.druidnet.ui

import android.content.res.AssetManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.druidanet.druidnet.data.AppDatabase
import org.druidanet.druidnet.data.DocumentsRepository
import org.druidanet.druidnet.data.DruidNetUiState
import org.druidanet.druidnet.data.PreferencesState
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.data.bibliography.BibliographyDAO
import org.druidanet.druidnet.data.bibliography.BibliographyEntity
import org.druidanet.druidnet.data.plant.PlantDAO
import org.druidanet.druidnet.data.plant.PlantData
import org.druidanet.druidnet.data.plant.PlantsRepository
import org.druidanet.druidnet.model.Confusion
import org.druidanet.druidnet.model.LanguageEnum
import org.druidanet.druidnet.model.Name
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.model.PlantCard
import org.druidanet.druidnet.model.Usage
import org.druidanet.druidnet.utils.mergeOrderedLists
import org.druidanet.druidnet.workers.KEY_GLOSSARY_UPDATED
import org.druidanet.druidnet.workers.KEY_PLANTS_DB_UPDATED
import org.druidanet.druidnet.workers.KEY_RECOMMENDATIONS_UPDATED
import org.druidanet.druidnet.workmanager.WorkManagerRepository
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DruidNetViewModel @Inject constructor(
    private val plantDao: PlantDAO,
    private val biblioDao: BibliographyDAO,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val plantsRepository: PlantsRepository,
    private val documentsRepository: DocumentsRepository,
    private val appDatabase: AppDatabase,
    private val workManagerRepository: WorkManagerRepository
) : ViewModel() {

    /****** STATE VARIABLES *****/

    private val _uiState = MutableStateFlow(DruidNetUiState())
    val uiState: StateFlow<DruidNetUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

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
    var allPlantsFlow = getAllPlants(LANGUAGE_APP)

    init {
        viewModelScope.launch {
            workManagerRepository.databaseUpdateWorkInfo.asFlow().collect { workInfos ->
                // For unique work, the list should contain one WorkInfo instance, or be empty.
                val workInfo = workInfos.firstOrNull()
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> {
                            // You might not want to show a Snackbar for this,
                            // or a very brief one if checkAndUpdateDatabase is user-initiated.
                            // _snackbarMessage.value = "Database update scheduled."
                        }
                        WorkInfo.State.RUNNING -> {
                            _snackbarMessage.value = "Comprobando actualizaciones de la base de datos..."
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            // Check if the database was actually updated or already up-to-date.
                            val outputData = workInfo.outputData
                            val updatedItems = mutableListOf<String>()
                            if (outputData.getBoolean(KEY_PLANTS_DB_UPDATED, false)) updatedItems.add("Base de datos de plantas")
                            if (outputData.getBoolean(KEY_RECOMMENDATIONS_UPDATED, false)) updatedItems.add("Recomendaciones")
                            if (outputData.getBoolean(KEY_GLOSSARY_UPDATED, false)) updatedItems.add("Glosario")
                            if (updatedItems.isEmpty()) {
                                _snackbarMessage.value = "¡La base de datos está al día!\nNada que actualizar"
                            } else {
                                _snackbarMessage.value = "¡Se actualizó: ${updatedItems.joinToString(separator = ", ")} !"
                            }
                        }
                        WorkInfo.State.FAILED -> {
                             _snackbarMessage.value = "Error actualizando la base de datos"
                            // Optionally, retrieve a more specific error message from workInfo.outputData
                            // val errorMessage = workInfo.outputData.getString("ERROR_MESSAGE_KEY")
                            // _snackbarMessage.value = errorMessage ?: "Database update failed."
                        }
                        WorkInfo.State.BLOCKED -> {
//                            _snackbarMessage.value = "Todavía no se puede actualizar la base de datos"
                        }
                        WorkInfo.State.CANCELLED -> {
                            _snackbarMessage.value = "Se ha cancelado la actualización de la base de datos."
                        }
                    }
                }
            }
        }
    }

    /****** USER INTERACTION (UI) FUNCTIONS *****/

    fun onSnackbarMessageShown() {
        _snackbarMessage.value = null
    }

    /****** NETWORK FUNCTIONS *****/
    fun checkAndUpdateDatabase() {
        Log.i("DruidNetViewModel", "Requesting database update via WorkManagerRepository.")
        workManagerRepository.startDatabaseUpdateWork()
    }

    /****** DATABASE FUNCTIONS *****/

    fun getPlantsFilteredByName(queryName: String): Flow<List<PlantCard>> {
        return if (queryName.isNotEmpty()) plantsRepository.searchPlantsByName(
            name = queryName,
            originalListPlants = allPlantsFlow
        ) else
            allPlantsFlow
    }


    // Get all plants from db
    // TODO: move getAllPlants function to PlantRepository
    fun getAllPlants(language: LanguageEnum): Flow<List<PlantCard>> =
        if (language != LanguageEnum.LATIN) {
            combine(
                plantDao.getPlantCatalogData(language),
                plantDao.getPlantCatalogLatinNotInLanguage(language)
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

    fun clearDB() =
        appDatabase.clearAllTables() // If AppDatabase doesn't have this, adjust to appDatabase.runInTransaction { plantDao.clear(); biblioDao.clear(); ... }


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
            allPlantsFlow = getAllPlants(language)
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

    /*
    fun onSearchQueryChanged(query: String) {
        _searchText.value = query
        Log.i( "D", query)
    }
    */

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
