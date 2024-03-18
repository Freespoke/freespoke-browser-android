/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokeaccount

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mozilla.fenix.apiservice.FreespokeApi
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.freespokeaccount.profile.ProfileUiModel
import org.mozilla.fenix.freespokeaccount.profile.ProfileUiModel.Companion.mapToUiProfile
import org.mozilla.fenix.freespokeaccount.store.FreespokeProfileStore
import org.mozilla.fenix.freespokeaccount.store.UpdateProfileAction
import org.mozilla.fenix.utils.Settings

class FreespokeProfileViewModel(
    freespokeProfileStore: FreespokeProfileStore,
    //userRepository: UserPreferenceRepository,
    settings: Settings,
) : ViewModel() {

    private val _profileData: MutableStateFlow<ProfileUiModel?> = MutableStateFlow(null)
    val profileData = _profileData.asStateFlow()
    private val _adsBlockEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val adsBlockEnabled = _adsBlockEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            _adsBlockEnabled.value = settings.adsBlockFeatureEnabled
            try {
                val profile = FreespokeApi.getUserProfileData()
                _profileData.value = profile.mapToUiProfile()
                freespokeProfileStore.dispatch(
                    UpdateProfileAction(profile)
                )

//                todo remove placeholder above
//                val accessToken = userRepository.getAccessToken() ?: return@launch
//                val profileResponse = FreespokeApi.service.getProfile(accessToken)
//
//                if (profileResponse.isSuccessful) {
//                    profileResponse.body()?.let { profile ->
//                        _profileData.value = profile.mapToUiProfile()
//                        freespokeProfileStore.dispatch(
//                            UpdateProfileAction(profile)
//                        )
//                    }
//                }
            } catch (e: Exception) {
                Log.e("API", e.localizedMessage ?: "")
            }
        }
    }

    fun updateAdsBlockState(isEnabled: Boolean) {
        viewModelScope.launch {
            _adsBlockEnabled.emit(isEnabled)
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return FreespokeProfileViewModel(
                    application.components.freespokeProfileStore,
                    //UserPreferenceRepository(context = application.baseContext),
                    application.components.settings
                ) as T
            }
        }
    }
}
