package org.mozilla.fenix.apiservice

import okhttp3.ResponseBody
import org.mozilla.fenix.apiservice.model.SignUpErrorResponse
import org.mozilla.fenix.apiservice.model.UserData
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException


class ErrorUtils {

    companion object {
        fun parseSignUpError(response: Response<UserData>) : SignUpErrorResponse? {
            val converter: Converter<ResponseBody, SignUpErrorResponse> = retrofit
                .responseBodyConverter(SignUpErrorResponse::class.java, arrayOfNulls<Annotation>(0))

            val error = try {
                response.errorBody()?.let { converter.convert(it) }
            } catch (e: IOException) {
                return null
            }
            return error

        }
    }
}
