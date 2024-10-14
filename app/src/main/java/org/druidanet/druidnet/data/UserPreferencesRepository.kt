package org.druidanet.druidnet.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository (
    private val dataStore: DataStore<Preferences>
){
    private companion object {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        const val TAG = "UserPreferencesRepo"
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
//                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            preferences -> preferences[IS_FIRST_LAUNCH] ?: true // initial value to true
        }

    suspend fun unsetFirstLaunch() {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = false
        }
    }
}