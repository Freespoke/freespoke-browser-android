/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.view

import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import com.google.accompanist.insets.statusBarsPadding
import mozilla.telemetry.glean.private.NoExtras
import org.mozilla.fenix.R
import org.mozilla.fenix.onboarding.viewmodel.AccountViewModel
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.GleanMetrics.Onboarding as OnboardingMetrics


/**
 * Enum that represents the onboarding screen that is displayed.
 */
enum class UpgradeOnboardingState {
    Welcome,
    DefaultBrowser,
    DefaultBrowserShow,
    Notifications,
    NotificationsSetup,
    Login,
    Registration,
    RegistrationWithErrors,
    Subscriptions,
    Premium,
    CompleteOnboarding
}

enum class OnboardingAppSettings {
    Notifications,
    SetupDefaultBrowser,
    Login
}

/**
 * A screen for displaying a welcome and sync sign in onboarding.
 *
 * @param isSyncSignIn Whether or not the user is signed into their Firefox Sync account.
 * @param onDismiss Invoked when the user clicks on the close or "Skip" button.
 * @param onSignInButtonClick Invoked when the user clicks on the "Sign In" button
 */
@Composable
fun UpgradeOnboarding(
    onDismiss: () -> Unit,
    viewModel: AccountViewModel,
    onSetupSettingsClick: ((OnboardingAppSettings, (() -> Unit)?) -> Unit)?,
    context: Context? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection()) {
        UpgradeOnboardingContent(
            onDismiss = onDismiss,
            viewModel = viewModel,
            onSetupSettingsClick,
            context
        )
    }
}

@Composable
private fun UpgradeOnboardingContent(
    onDismiss: () -> Unit,
    viewModel: AccountViewModel,
    onSetupSettingsClick: ((OnboardingAppSettings, (() -> Unit)?) -> Unit)?,
    context: Context? = null,
) {
    var onboardingState by remember { mutableStateOf(UpgradeOnboardingState.Welcome) }

    Column(
        modifier = Modifier
            .background(FirefoxTheme.colors.layer1)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .fillMaxSize(),
    ) {
        OnboardingPage(
            pageState = when (onboardingState) {
                UpgradeOnboardingState.Welcome -> OnboardingPageState(
                    type = onboardingState,
                    onRecordImpressionEvent = {
                        OnboardingMetrics.welcomeCardImpression.record(NoExtras())
                    },
                )

                UpgradeOnboardingState.DefaultBrowser, UpgradeOnboardingState.DefaultBrowserShow -> OnboardingPageState(
                    type = onboardingState,
                    image = R.drawable.onboarding_default_browser,
                    title = stringResource(id = R.string.onboarding_default_browser_title),
                    description = stringResource(id = R.string.onboarding_default_browser_description),
                    primaryButtonText = stringResource(id = R.string.set_as_default_browser),
                    secondaryButtonText = stringResource(id = R.string.onboarding_default_browser_skip),
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )

                UpgradeOnboardingState.Notifications, UpgradeOnboardingState.NotificationsSetup -> OnboardingPageState(
                    type = onboardingState,
                    image = R.drawable.onboarding_notification_settings,
                    title = stringResource(id = R.string.onboarding_notification_settings_title),
                    description = stringResource(id = R.string.onboarding_notification_settings_description),
                    primaryButtonText = stringResource(id = R.string.onboarding_next_button),
                    secondaryButtonText = stringResource(id = R.string.onboarding_notification_settings_skip),
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )

                UpgradeOnboardingState.Login -> OnboardingPageState(
                    type = onboardingState,
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )
                UpgradeOnboardingState.Registration -> OnboardingPageState(
                    type = onboardingState,
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )
                UpgradeOnboardingState.Subscriptions -> OnboardingPageState(
                    type = onboardingState,
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )
                UpgradeOnboardingState.CompleteOnboarding -> OnboardingPageState(
                    type = onboardingState,
                    image = R.drawable.ic_onboarding_completion,
                    title = stringResource(id = R.string.onboarding_complete_title),
                    description = stringResource(id = R.string.onboarding_complete_description),
                    primaryButtonText = stringResource(id = R.string.onboarding_finish),
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )
                UpgradeOnboardingState.RegistrationWithErrors -> OnboardingPageState(
                    type = onboardingState,
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )
                UpgradeOnboardingState.Premium -> OnboardingPageState(
                    type = onboardingState,
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )
            },
            onDismiss = {
                onDismiss()
            },
            updatedOnboardingState = {
                when (it) {
                    UpgradeOnboardingState.DefaultBrowserShow -> {
                        onSetupSettingsClick?.invoke(OnboardingAppSettings.SetupDefaultBrowser) {
                            onboardingState = UpgradeOnboardingState.Notifications
                        }
                    }
                    UpgradeOnboardingState.NotificationsSetup -> {
                        onSetupSettingsClick?.invoke(OnboardingAppSettings.Notifications) {
                            onboardingState = UpgradeOnboardingState.CompleteOnboarding
                        }
                    }
                    UpgradeOnboardingState.Login -> {
                        onSetupSettingsClick?.invoke(OnboardingAppSettings.Login) {
                            onboardingState = UpgradeOnboardingState.Subscriptions
                        }
                    }
                    else -> onboardingState = it
                }
            },
            viewModel = viewModel,
            modifier = Modifier.weight(1f),
            context
        )
    }
}

/**
 * Force Left to Right layout direction when running on Android API level < 23 (Android 5.1).
 * Bug with compose and RTL views causing crash in the Onboarding screen in Android 5.1.
 * Bugzilla link: https://bugzilla.mozilla.org/show_bug.cgi?id=1792796
 */
@Composable
private fun layoutDirection() = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
    LocalLayoutDirection.current
} else {
    LayoutDirection.Ltr
}
