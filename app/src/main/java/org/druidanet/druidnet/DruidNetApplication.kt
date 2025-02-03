package org.druidanet.druidnet

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModel
import org.druidanet.druidnet.data.AppDatabase
import org.druidanet.druidnet.data.UserPreferencesRepository
import org.druidanet.druidnet.model.LanguageEnum

public val DEFAULT_LANGUAGE = LanguageEnum.CASTELLANO // Should be set to the system language

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