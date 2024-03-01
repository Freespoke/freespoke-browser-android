package org.mozilla.fenix.domain.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import mozilla.components.support.ktx.android.content.stringPreference
import org.mozilla.fenix.apiservice.model.UserData

class UserPreferenceRepository(
    private val dataStore: DataStore<Preferences>
) {

    private object PreferencesKeys {
        val USER_ACCESS_TOKEN = stringPreferencesKey("access_token")
        val USER_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
    }

    suspend fun writeUserData(userData: UserData) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ACCESS_TOKEN] = userData.accessToken
            preferences[PreferencesKeys.USER_REFRESH_TOKEN] = userData.refreshToken
            preferences[PreferencesKeys.USER_ID] = userData.id
        }
    }

}
