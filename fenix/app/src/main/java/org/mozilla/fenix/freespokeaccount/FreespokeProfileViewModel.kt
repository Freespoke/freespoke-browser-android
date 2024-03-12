package org.mozilla.fenix.freespokeaccount

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mozilla.fenix.apiservice.FreespokeApi
import org.mozilla.fenix.freespokeaccount.profile.ProfileUiModel
import org.mozilla.fenix.freespokeaccount.profile.ProfileUiModel.Companion.mapToUiProfile

class FreespokeProfileViewModel() : ViewModel() {

    private val _profileData: MutableStateFlow<ProfileUiModel?> = MutableStateFlow(null)
    val profileData = _profileData.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val profile = FreespokeApi.getUserProfileData()
                _profileData.value = profile.mapToUiProfile()
            } catch (e: Exception) {
                Log.e("API", e.localizedMessage ?: "")
            }
        }
    }

    companion object {

    }
}
