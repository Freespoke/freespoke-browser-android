package org.mozilla.fenix.utils

import android.net.Uri
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import org.mozilla.fenix.apiservice.model.UserData
import org.mozilla.fenix.domain.repositories.UserPreferenceRepository


class AuthManager {

    private var authState = AuthState()

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

    fun requestAccessToken(
        service: AuthorizationService,
        resp: AuthorizationResponse,
        userRepository: UserPreferenceRepository,
        onTokenResponse: (Boolean) -> Unit,
    ) {
        val tokenExchangeRequest = resp.createTokenExchangeRequest()

        service.performTokenRequest(tokenExchangeRequest) { response, exception ->
            response?.let {
                authState.update(response, exception)

                val jwt = it.idToken?.let { idToken ->
                    JWT(idToken)
                }
                jwt?.let { token ->
                    runBlocking {
                        val userId = token.getClaim("external_account_id").asString()
                        val userData = UserData(
                            id = userId ?: "",
                            accessToken = it.accessToken ?: "",
                            refreshToken = it.refreshToken ?: ""
                        )
                        userRepository.writeUserData(userData)
                    }
                }
            }
            onTokenResponse(isUserAuthorized())
        }
    }

    fun updateAuthState(resp: AuthorizationResponse?, ex: AuthorizationException?) {
        authState.update(resp, ex);
    }

    fun isUserAuthorized() = authState.isAuthorized

    fun makeTokenRefreshRequest(authService: AuthorizationService, listener: (Boolean) -> Unit) {
        authService.performTokenRequest(
            authState.createTokenRefreshRequest(),
        ) { resp, ex ->
            authState.update(resp, ex)
            listener.invoke(resp != null)
        }
    }
}
