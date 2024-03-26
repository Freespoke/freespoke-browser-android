package org.mozilla.fenix.onboarding.viewmodel

import org.mozilla.fenix.apiservice.model.FieldsError

data class SignUpUiState(
    var errorData: FieldsError = FieldsError(),
    val isSuccessfulSignUp: Boolean = false
)
