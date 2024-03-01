/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.view

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import mozilla.telemetry.glean.private.NoExtras
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.annotation.LightDarkPreview
import org.mozilla.fenix.onboarding.AccountViewModel
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
    SetupDefaultBrowser
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
    isSyncSignIn: Boolean,
    onDismiss: () -> Unit,
    viewModel: AccountViewModel?,
    onSetupSettingsClick: ((OnboardingAppSettings) -> Unit)? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection()) {
        UpgradeOnboardingContent(
            isSyncSignIn = isSyncSignIn,
            onDismiss = onDismiss,
            viewModel = viewModel,
            onSetupSettingsClick
        )
    }
}

@Composable
private fun UpgradeOnboardingContent(
    isSyncSignIn: Boolean,
    onDismiss: () -> Unit,
    viewModel: AccountViewModel?,
    onSetupSettingsClick: ((OnboardingAppSettings) -> Unit)?
) {
    var onboardingState by remember { mutableStateOf(UpgradeOnboardingState.Welcome) }

    Column(
        modifier = Modifier
            .background(FirefoxTheme.colors.layerOnboarding)
            .fillMaxSize()
            .statusBarsPadding(),
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
                    image = R.drawable.ic_onboarding_tabs,
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
            onPrimaryButtonClick = {
                when (it) {
                    UpgradeOnboardingState.DefaultBrowserShow -> onSetupSettingsClick?.invoke(OnboardingAppSettings.SetupDefaultBrowser)
                    UpgradeOnboardingState.NotificationsSetup -> onSetupSettingsClick?.invoke(OnboardingAppSettings.Notifications)
                    else -> onboardingState = it
                }
            },
            viewModel = viewModel,
        )

        if (isSyncSignIn) {
            Spacer(modifier = Modifier.height(6.dp))
        } else {
            Indicators(onboardingState = onboardingState)
        }
    }
}

@Composable
private fun Indicators(
    onboardingState: UpgradeOnboardingState,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Indicator(
            color = if (onboardingState == UpgradeOnboardingState.Welcome) {
                FirefoxTheme.colors.indicatorActive
            } else {
                FirefoxTheme.colors.indicatorInactive
            },
        )

        Spacer(modifier = Modifier.width(28.dp))

    /*    Indicator(
            color = if (onboardingState == UpgradeOnboardingState.Newsfeed) {
                FirefoxTheme.colors.indicatorActive
            } else {
                FirefoxTheme.colors.indicatorInactive
            },
        )

        Spacer(modifier = Modifier.width(28.dp))

        Indicator(
            color = if (onboardingState == UpgradeOnboardingState.Shop) {
                FirefoxTheme.colors.indicatorActive
            } else {
                FirefoxTheme.colors.indicatorInactive
            },
        )

        Spacer(modifier = Modifier.width(28.dp))

        Indicator(
            color = if (onboardingState == UpgradeOnboardingState.Search) {
                FirefoxTheme.colors.indicatorActive
            } else {
                FirefoxTheme.colors.indicatorInactive
            },
        )

        Spacer(modifier = Modifier.width(28.dp))

        Indicator(
            color = if (onboardingState == UpgradeOnboardingState.Tabs) {
                FirefoxTheme.colors.indicatorActive
            } else {
                FirefoxTheme.colors.indicatorInactive
            },
        )*/

    }
}

@Composable
private fun Indicator(color: Color) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
@LightDarkPreview
private fun OnboardingPreview() {
    FirefoxTheme {
        UpgradeOnboarding(
            isSyncSignIn = false,
            onDismiss = {},
            viewModel = null
        ) {
        }
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
