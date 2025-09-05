package org.druidanet.druidnet.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.druidanet.druidnet.data.AppDatabase
import org.druidanet.druidnet.data.bibliography.BibliographyDAO
import org.druidanet.druidnet.data.plant.PlantDAO
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context.applicationContext)
    }

    @Provides
    @Singleton
    fun providePlantDao(appDatabase: AppDatabase): PlantDAO {
        return appDatabase.plantDao()
    }

    @Provides
    @Singleton
    fun provideBibliographyDao(appDatabase: AppDatabase): BibliographyDAO {
        return appDatabase.biblioDao()
    }
}