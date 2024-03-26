package org.mozilla.fenix.apiservice.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignUpErrorResponse(
    @Json(name = "message") val message: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "code") val code: String?,
    @Json(name = "details") val errorDetails: Details?
)

@JsonClass(generateAdapter = true)
data class Details(
    @Json(name = "fieldErrors") val fieldsError: FieldsError,
    @Json(name = "formErrors") val formErrors: List<String>
)

@JsonClass(generateAdapter = true)
data class FieldsError(
    @Json(name = "firstName") val firstName: List<String> = listOf(),
    @Json(name = "lastName") val lastName: List<String> = listOf(),
    @Json(name = "email") val email: List<String> = listOf(),
    @Json(name = "password") val password: List<String> = listOf(),
)
