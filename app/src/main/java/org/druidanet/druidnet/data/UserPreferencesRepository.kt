package org.druidanet.druidnet.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.druidanet.druidnet.model.LanguageEnum
import java.io.IOException
import java.util.Locale

private val DEFAULT_LANGUAGE = when (Locale.getDefault().language) {
    "es" -> LanguageEnum.CASTELLANO
    "ca" -> LanguageEnum.CATALAN
    "ga" -> LanguageEnum.GALLEGO
    "eu" -> LanguageEnum.EUSKERA
    else -> LanguageEnum.CASTELLANO
}

data class UserPreferences (
    val displayLanguage: LanguageEnum
)

class UserPreferencesRepository (
    private val dataStore: DataStore<Preferences>
){
    private companion object {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val DISPLAY_NAME_LANGUAGE = stringPreferencesKey("display_name_language")
        val DATABASE_VERSION = longPreferencesKey ("database_version")
        val GLOSSARY_VERSION = longPreferencesKey ("glossary_version")
        val RECOMMENDATIONS_VERSION = longPreferencesKey ("recommendations_version")
        const val TAG = "UserPreferencesRepo"
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
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


    // ALTERNATIVE WAY TO OBTAIN ALL USER PREFERENCES AT ONCE (TO BE USED LATER)
//    fun mapUserPreferences (preferences: Preferences): UserPreferences {
//        val displayNameLanguage = dataStore.data
//            .catch {
//                if (it is IOException) {
//                    Log.e(TAG, "Error reading preferences.", it)
//                    emit(emptyPreferences())
//                } else {
//                    throw it
//                }
//            }
//            .map { preferences ->
//                LanguageEnum.valueOf(preferences[DISPLAY_NAME_LANGUAGE]?: DEFAULT_LANGUAGE.toString())
//            }
//        return UserPreferences(displayNameLanguage)
//    }

//    val getUserPreferencesFlow: Flow<UserPreferences> = dataStore.data
//        .catch {
//            if (it is IOException) {
//                Log.e(TAG, "Error reading preferences.", it)
//                emit(emptyPreferences())
//            } else {
//                throw it
//            }
//        }
//        .map { preferences ->
//            val displayLanguagePreference = LanguageEnum.valueOf(preferences[DISPLAY_NAME_LANGUAGE]?: DEFAULT_LANGUAGE.toString())
//            val isFirstLaunchPreference = preferences[IS_FIRST_LAUNCH] ?: true
//            UserPreferences(displayLanguage = displayLanguagePreference)
//        }
//
//    suspend fun fetchInitialPreferences(): UserPreferences =
//        getUserPreferencesFlow.first()


    val getDisplayNameLanguagePreference: Flow<LanguageEnum> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            if(preferences[DISPLAY_NAME_LANGUAGE] != null)
                LanguageEnum.valueOf(preferences[DISPLAY_NAME_LANGUAGE]!!)
            else DEFAULT_LANGUAGE
        }


    suspend fun updateDisplayNameLanguagePreference(language: LanguageEnum) {
        dataStore.edit { preferences ->
            preferences[DISPLAY_NAME_LANGUAGE] = language.toString()
        }
    }

    val getDatabaseVersion: Flow<Long> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[DATABASE_VERSION]?:0 // Inital value to 0
        }

    val getGlossaryVersion: Flow<Long> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[GLOSSARY_VERSION]?:0 // Inital value to 0
        }


    val getRecommendationsVersion: Flow<Long> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[RECOMMENDATIONS_VERSION]?:0 // Inital value to 0
        }


    suspend fun updateDatabaseVersion(newVersion: Long) = updateVersion("database", newVersion)

    suspend fun updateVersion(versionPreference: String, newVersion: Long) {
        val versionKey = when (versionPreference) {
            "database" -> DATABASE_VERSION
            "glossary" -> GLOSSARY_VERSION
            "recommendations" -> RECOMMENDATIONS_VERSION
            else -> DATABASE_VERSION
        }

        dataStore.edit { preferences ->
            preferences[versionKey] = newVersion
        }
    }

}