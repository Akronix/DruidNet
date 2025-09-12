package org.druidanet.druidnet.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.AssetManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.druidanet.druidnet.data.DocumentsRepository
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.data.bibliography.BibliographyRepository
import org.druidanet.druidnet.data.images.ImagesLocalDataSource
import org.druidanet.druidnet.data.images.ImagesRemoteDataSource
import org.druidanet.druidnet.data.images.ImagesRepository
import org.druidanet.druidnet.data.plant.PlantDAO
import org.druidanet.druidnet.data.plant.PlantsRemoteDataSource
import org.druidanet.druidnet.data.plant.PlantsRepository
import org.druidanet.druidnet.network.BackendApiService
import org.druidanet.druidnet.network.BackendScalarApiService
import org.druidanet.druidnet.workmanager.WorkManagerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>): UserPreferencesRepository {
        // Assuming UserPreferencesRepository constructor takes DataStore<Preferences>
        return UserPreferencesRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideDocumentsRepository(
        @ApplicationContext context: Context,
        assetsManager: AssetManager,
        backendScalarApiService: BackendScalarApiService,
    ): DocumentsRepository {
        // Assuming DocumentsRepository constructor takes BackendApiService and PlantDAO
        return DocumentsRepository(
            backendScalarApiService,
            localStorageDir = context.getDir("documents", MODE_PRIVATE).absolutePath,
            assetsMgr = assetsManager
        )
    }

    @Provides
    @Singleton
    fun provideImagesRepository(
        @ApplicationContext context: Context,
        assetsManager: AssetManager,
        backendScalarApiService: BackendScalarApiService,
    ): ImagesRepository {
        // Assuming ImagesRepository constructor takes Context, BackendScalarApiService, and PlantDAO
        return ImagesRepository(ImagesRemoteDataSource(backendScalarApiService), ImagesLocalDataSource(
            assetsManager,
            context.getDir("images", MODE_PRIVATE).absolutePath
        ))
    }

    @Provides
    @Singleton
    fun providePlantsRepository(
        plantDAO: PlantDAO,
        backendApiService: BackendApiService,
    ): PlantsRepository {
        // Assuming ImagesRepository constructor takes Context, BackendScalarApiService, and PlantDAO
        return PlantsRepository(PlantsRemoteDataSource(backendApiService), plantDao = plantDAO)
    }

    @Provides
    @Singleton
    fun provideBibliographyRepository(
       backendApiService: BackendApiService
    ): BibliographyRepository {
        // Assuming BibliographyRepository constructor takes BackendApiService and BibliographyDAO
        return BibliographyRepository(backendApiService)
    }

    @Provides
    @Singleton
    fun provideWorkManagerRepository(
        @ApplicationContext context: Context
    ): WorkManagerRepository {
        return WorkManagerRepository(context)
    }

}

