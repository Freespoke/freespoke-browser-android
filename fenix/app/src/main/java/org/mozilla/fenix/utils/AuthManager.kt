/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import org.json.JSONException
import org.mozilla.fenix.BuildConfig
import org.mozilla.fenix.apiservice.model.UserData
import org.mozilla.fenix.domain.repositories.UserPreferenceRepository


class AuthManager(
    private val context: Context,
    private val userRepository: UserPreferenceRepository
) {

    private var authState = AuthState()
    private var authService: AuthorizationService? = null
    private val authServiceConfig: AuthorizationServiceConfiguration by lazy {
        AuthorizationServiceConfiguration(
            Uri.parse(BuildConfig.AUTH_URL), // authorization endpoint
            Uri.parse(BuildConfig.REFRESH_TOKEN_URL) // token endpoint
            //todo logout endpoint
        )
    }

    private val _tokenRefreshTrigger: MutableSharedFlow<Unit> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val tokenRefreshTrigger = _tokenRefreshTrigger.asSharedFlow()


    fun initAuthManager() {
        authService = AuthorizationService(context.applicationContext)
        initAuthState()
    }

    private fun initAuthState() {
        val authStateJson = runBlocking {
            userRepository.getAuthState()
        }

        authStateJson?.let {
            try {
                authState = AuthState.jsonDeserialize(it)

                if (authState.needsTokenRefresh) {
                    makeTokenRefreshRequest()
                } else {
                    _tokenRefreshTrigger.tryEmit(Unit)
                }
            } catch (_: JSONException) {}
        } ?: run {
            _tokenRefreshTrigger.tryEmit(Unit)
        }
    }

    private fun saveAuthState() {
        val authStateJson = authState.jsonSerializeString()
        runBlocking {
            userRepository.writeAuthState(authStateJson)
        }
    }

    fun prepareAuthRequestIntent(): Intent? {
        val authRequestBuilder = AuthorizationRequest.Builder(
            authServiceConfig,  // the authorization service configuration
            "mobile",  // the client ID, typically pre-registered and static
            ResponseTypeValues.CODE,  // the response_type value: we want a code
            Uri.parse("com.freespoke:/androidappcallbackauth"),
        )// the redirect URI to which the auth response is sent

        val authRequest = authRequestBuilder
            .setScope("openid")
            .build()

        return authService?.getAuthorizationRequestIntent(authRequest)
    }

    fun processAuthResponse(intent: Intent?, onTokenResponse: (Boolean) -> Unit) {
        val oAuthResponse = AuthorizationResponse.fromIntent(intent!!)
        val oAuthException = AuthorizationException.fromIntent(intent)
        authState.update(oAuthResponse, oAuthException)

        if (oAuthResponse != null) {
            saveAuthState()
            requestAccessToken(oAuthResponse, onTokenResponse)
        } else {
            clearAuthState()
            onTokenResponse(false)
        }
    }

    private fun requestAccessToken(
        oAuthResponse: AuthorizationResponse,
        onTokenResponse: (Boolean) -> Unit,
    ) {
        val tokenExchangeRequest = oAuthResponse.createTokenExchangeRequest()

        authService?.performTokenRequest(tokenExchangeRequest) { response, exception ->
            authState.update(response, exception)
            response?.let {
                saveAuthState()
            }
            exception?.let {
                clearAuthState()
            }
            onTokenResponse(isUserAuthorized())
        } ?: run {
            onTokenResponse(false)
        }
    }

    fun isUserAuthorized() = authState.isAuthorized

    private fun makeTokenRefreshRequest() {
        authService?.performTokenRequest(
            authState.createTokenRefreshRequest(),
        ) { resp, ex ->
            authState.update(resp, ex)
            resp?.let {
                saveAuthState()
            }
            ex?.let {
                clearAuthState()
            }
            _tokenRefreshTrigger.tryEmit(Unit)
        }
    }

    fun refreshTokenAfterSingUp(userData: UserData) {
        val tokenRefreshRequest = TokenRequest.Builder(
            authServiceConfig,
            "public",
        ).setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setScope(null)
            .setRefreshToken(userData.refreshToken)
            .setAdditionalParameters(emptyMap())
            .build()

        val authResponse = AuthorizationResponse.Builder(
            AuthorizationRequest.Builder(
                authServiceConfig,
                "public",
                ResponseTypeValues.CODE,
                Uri.parse("com.freespoke:/androidappcallbackauth"),
            ).build()
        ).build()

        authState.update(authResponse, null)

        authService?.performTokenRequest(tokenRefreshRequest) { resp, ex ->
            authState.update(resp, ex)
            resp?.let {
                saveAuthState()
            }
            ex?.let {
                clearAuthState()
            }
        }
    }

    suspend fun performApiCallWithFreshTokens(
        scope: CoroutineScope,
        onError: (() -> Unit)? = null,
        action: suspend (String, String) -> Unit,
    ) {
        tokenRefreshTrigger.first()
        authService?.let {
            authState.performActionWithFreshTokens(it) { accessToken, idToken, exception ->
                if (exception != null) {
                    onError?.invoke()
                } else {
                    scope.launch {
                        action(accessToken ?: "", idToken ?: "")
                    }
                    saveAuthState()
                }
            }
        } ?: run {
            onError?.invoke()
        }
    }

    private fun clearAuthState() {
        runBlocking {
            userRepository.clearAuthState()
        }
    }
}
