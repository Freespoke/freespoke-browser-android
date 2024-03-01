package org.mozilla.fenix.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import org.mozilla.fenix.apiservice.FreespokeApi
import org.mozilla.fenix.domain.repositories.UserPreferenceRepository

class AccountViewModel(/*val repository: UserPreferenceRepository*/): ViewModel() {

    val isSuccessResult: LiveData<Boolean>? = null

    fun signUp(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            FreespokeApi.service.signUpUser(firstName, lastName, email, password)
           // repository.writeUserData(userData)
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                //val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return AccountViewModel() as T
            }
        }
    }
}
