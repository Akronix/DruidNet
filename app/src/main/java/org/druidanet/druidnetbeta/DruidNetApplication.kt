package org.druidanet.druidnetbeta

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.druidanet.druidnetbeta.data.AppDatabase
import org.druidanet.druidnetbeta.data.UserPreferencesRepository
import org.druidanet.druidnetbeta.model.LanguageEnum

public val LANGUAGE_APP = LanguageEnum.CASTELLANO

private const val USER_PREFERENCES_NAME = "preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class DruidNetApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(dataStore) }

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }

}