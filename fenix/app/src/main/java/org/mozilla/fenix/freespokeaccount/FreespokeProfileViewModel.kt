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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import mozilla.components.lib.state.ext.flow
import org.mozilla.fenix.apiservice.FreespokeApi
import org.mozilla.fenix.domain.repositories.UserPreferenceRepository
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.freespokeaccount.profile.ProfileUiModel
import org.mozilla.fenix.freespokeaccount.profile.ProfileUiModel.Companion.mapToUiProfile
import org.mozilla.fenix.freespokeaccount.store.FreespokeProfileStore
import org.mozilla.fenix.freespokeaccount.store.UpdateProfileAction

class FreespokeProfileViewModel(
    freespokeProfileStore: FreespokeProfileStore,
    userRepository: UserPreferenceRepository
) : ViewModel() {

    private val _profileData: MutableStateFlow<ProfileUiModel?> = MutableStateFlow(null)
    val profileData = _profileData.asStateFlow()

    init {
        freespokeProfileStore.flow()
            .onEach {
                _profileData.value = it.profile?.mapToUiProfile()
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            try {
                userRepository.getAccessTokenFlow().collectLatest {
                    //todo if(!userLoggedIn) profileData.value = null
                    val profileResponse = FreespokeApi.service.getProfile("Bearer $it")

                    if (profileResponse.isSuccessful) {
                        profileResponse.body()?.let { profile ->
                            _profileData.value = profile.mapToUiProfile()
                            freespokeProfileStore.dispatch(
                                UpdateProfileAction(profile)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", e.localizedMessage ?: "")
            }
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
                    UserPreferenceRepository(context = application.baseContext)
                ) as T
            }
        }
    }
}
