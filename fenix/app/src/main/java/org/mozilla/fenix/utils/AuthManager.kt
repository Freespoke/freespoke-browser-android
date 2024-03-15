package org.mozilla.fenix.utils

import android.net.Uri
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

class AuthManager {

    companion object {

        fun prepareAuthRequest(serviceConfig: AuthorizationServiceConfiguration): AuthorizationRequest {
            val authRequestBuilder = AuthorizationRequest.Builder(
                serviceConfig,  // the authorization service configuration
                "mobile",  // the client ID, typically pre-registered and static
                ResponseTypeValues.CODE,  // the response_type value: we want a code
                Uri.parse("com.freespoke:/androidappcallbackauth"),
            )// the redirect URI to which the auth response is sent

            return authRequestBuilder
                .setScope("openid")
                .build()
        }
    }
}
