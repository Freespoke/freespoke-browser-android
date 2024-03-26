package org.mozilla.fenix.onboarding.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mozilla.fenix.apiservice.ErrorUtils
import org.mozilla.fenix.apiservice.FreespokeApi
import org.mozilla.fenix.apiservice.model.FieldsError
import org.mozilla.fenix.apiservice.model.SignUpUserModel
import org.mozilla.fenix.domain.repositories.UserPreferenceRepository
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.utils.AuthManager

class AccountViewModel(
    private val repository: UserPreferenceRepository,
    private val authManager: AuthManager,
): ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun signUp(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            val userDataResponse = FreespokeApi.service.signUpUser(SignUpUserModel(firstName, lastName, email, password))
            if (userDataResponse.isSuccessful) {
                userDataResponse.body()?.let {
                    repository.writeUserData(it)
                    authManager.refreshTokenAfterSignUp(it)
                }
                _uiState.value = SignUpUiState(isSuccessfulSignUp = true)
            } else {
                val errorData = ErrorUtils.parseSignUpError(userDataResponse)

                errorData?.let {
                    _uiState.value = SignUpUiState(
                        isSuccessfulSignUp = false,
                        errorData = errorData.errorDetails?.fieldsError ?: FieldsError()
                    )
                }
            }
        }
    }

    fun saveToken(data: String?) {
        data?.let {
            Log.d("SignIn", "save data to dataStore - $data")
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
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return AccountViewModel(
                    UserPreferenceRepository(context = application.baseContext),
                    application.components.authManager
                ) as T
            }
        }
    }
}
