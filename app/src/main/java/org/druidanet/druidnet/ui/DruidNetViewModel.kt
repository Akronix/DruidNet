package org.druidanet.druidnet.ui

import android.util.Log // Keep Log for potential future use or if other methods use it
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import org.druidanet.druidnet.workmanager.WorkManagerRepository // Added this import
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

// WorkManager related imports are removed as they are now encapsulated in WorkManagerRepository

@HiltViewModel
class DruidNetViewModel @Inject constructor(
    private val plantDao: PlantDAO,
    private val biblioDao: BibliographyDAO,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val plantsRepository: PlantsRepository,
    private val documentsRepository: DocumentsRepository,
    private val appDatabase: AppDatabase,
    private val workManagerRepository: WorkManagerRepository // Changed from workManager to workManagerRepository
) : ViewModel() {

    /****** STATE VARIABLES *****/

    private val _uiState = MutableStateFlow(DruidNetUiState())
    val uiState: StateFlow<DruidNetUiState> = _uiState.asStateFlow()

    /*
    private val _searchText = MutableStateFlow("")
    val catalogSearchQuery = _searchText.asStateFlow()
    */

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

    /****** USER INTERACTION (UI) FUNCTIONS *****/

    /****** NETWORK FUNCTIONS *****/
    fun checkAndUpdateDatabase() {
        Log.i("DruidNetViewModel", "Requesting database update via WorkManagerRepository.")
        workManagerRepository.startDatabaseUpdateWork()
        // The detailed WorkManager logic (creating request, constraints, enqueueing)
        // is now handled within WorkManagerRepository.
        // You can still observe the work status via LiveData from WorkManagerRepository if needed:
        // For example, if WorkManagerRepository exposes LiveData<WorkInfo>
        // val workInfoLiveData = workManagerRepository.getDatabaseUpdateWorkInfo()
        // workInfoLiveData.observe(lifecycleOwner, { workInfo -> ... update UI ... })
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
