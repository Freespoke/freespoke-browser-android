package org.mozilla.fenix.domain.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import org.mozilla.fenix.apiservice.model.UserData

private const val PREFERENCES_NAME = "freespoke_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class UserPreferenceRepository(
    private val context: Context
) {

    private object PreferencesKeys {
        val USER_ACCESS_TOKEN = stringPreferencesKey("access_token")
        val USER_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
    }

    suspend fun writeUserData(userData: UserData) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ACCESS_TOKEN] = userData.accessToken
            preferences[PreferencesKeys.USER_REFRESH_TOKEN] = userData.refreshToken
            preferences[PreferencesKeys.USER_ID] = userData.id
        }
    }

}
