/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.view

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.onesignal.OneSignal
import mozilla.telemetry.glean.private.NoExtras
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.annotation.LightDarkPreview
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.GleanMetrics.Onboarding as OnboardingMetrics

/**
 * Enum that represents the onboarding screen that is displayed.
 */
enum class UpgradeOnboardingState {
    Welcome,
    Updates,
    Notifications,
    DefaultBrowser,
    Finish,
}

enum class OneSignalSegment {
    Shop,
    News,
    General
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
    onDefaultBrowserSetupClick: (() -> Unit)? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection()) {
        UpgradeOnboardingContent(
            isSyncSignIn = isSyncSignIn,
            onDismiss = onDismiss,
            onDefaultBrowserSetupClick = onDefaultBrowserSetupClick
        )
    }
}

@Composable
private fun UpgradeOnboardingContent(
    isSyncSignIn: Boolean,
    onDismiss: () -> Unit,
    onDefaultBrowserSetupClick: (() -> Unit)? = null
) {
    var onboardingState by remember { mutableStateOf(UpgradeOnboardingState.Welcome) }

    Column(
        modifier = Modifier
            .background(FirefoxTheme.colors.layerOnboarding)
            .fillMaxSize()
            .padding(bottom = 32.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        OnboardingPage(
            pageState = when (onboardingState) {
                UpgradeOnboardingState.Welcome -> OnboardingPageState(
                    type = onboardingState,
                    image = null,
                    title = stringResource(id = R.string.onboarding_home_welcome_updated_title),
                    description = stringResource(id = R.string.onboarding_welcome_description),
                    primaryButtonText = stringResource(id = R.string.onboarding_home_get_started_button),
                ) {
                    OnboardingMetrics.welcomeCardImpression.record(NoExtras())
                }

                UpgradeOnboardingState.Updates -> OnboardingPageState(
                    type = onboardingState,
                    image = null,
                    title = stringResource(id = R.string.onboarding_updates_title),
                    description = stringResource(id = R.string.onboarding_updates_decription),
                    primaryButtonText = stringResource(id = R.string.onboarding_next_button),
                    secondaryButtonText = null,
                ) {
                    OnboardingMetrics.syncCardImpression.record(NoExtras())
                }

                UpgradeOnboardingState.Notifications -> OnboardingPageState(
                    type = onboardingState,
                    image = R.drawable.onboarding_notification_settings,
                    title = stringResource(id = R.string.onboarding_notification_title),
                    description = stringResource(id = R.string.onboarding_notification_decription),
                    primaryButtonText = stringResource(id = R.string.onboarding_next_button),
                    secondaryButtonText = null,
                ) {
                    OnboardingMetrics.syncCardImpression.record(NoExtras())
                }

                UpgradeOnboardingState.DefaultBrowser -> OnboardingPageState(
                    type = onboardingState,
                    image = R.drawable.onboarding_default_browser,
                    title = stringResource(id = R.string.onboarding_default_browser_title),
                    description = stringResource(id = R.string.onboarding_default_browser_description),
                    primaryButtonText = stringResource(id = R.string.onboardin_set_as_default_browser),
                    secondaryButtonText = stringResource(id = R.string.skip_default_browser),
                ) {
                    OnboardingMetrics.syncCardImpression.record(NoExtras())
                }

                UpgradeOnboardingState.Finish -> OnboardingPageState(
                    type = onboardingState,
                    image = R.drawable.onboarding_finish,
                    title = stringResource(id = R.string.onboarding_finish_title),
                    description = "",
                    primaryButtonText = stringResource(id = R.string.start_searching),
                    secondaryButtonText = null,
                ) {
                    OnboardingMetrics.syncCardImpression.record(NoExtras())
                }
            },
            onDismiss = {
                onDismiss()
            },
            onPrimaryButtonClick = {
                when (onboardingState) {
                    UpgradeOnboardingState.Welcome -> {
                        onboardingState = UpgradeOnboardingState.Updates
                    }
                    UpgradeOnboardingState.Updates -> {
                        OneSignal.User.addTag(OneSignalSegment.News.name, if (it?.first == true) "1" else "0")
                        OneSignal.User.addTag(OneSignalSegment.Shop.name, if (it?.second == true) "1" else "0")
                        OneSignal.User.addTag(OneSignalSegment.General.name, if (it?.third == true) "1" else "0")

                        onboardingState = UpgradeOnboardingState.Notifications
                    }
                    UpgradeOnboardingState.Notifications -> {
                        onboardingState = UpgradeOnboardingState.DefaultBrowser
                    }
                    UpgradeOnboardingState.DefaultBrowser -> {
                        onDefaultBrowserSetupClick?.invoke()
                        onboardingState = UpgradeOnboardingState.Finish
                    }
                    UpgradeOnboardingState.Finish -> {
                        onDismiss()
                    }
                }
            },
            {
                if (onboardingState == UpgradeOnboardingState.DefaultBrowser) {
                    onboardingState = UpgradeOnboardingState.Finish
                }
            },
            modifier = Modifier.weight(1f),
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

        Indicator(
            color = if (onboardingState == UpgradeOnboardingState.Updates) {
                FirefoxTheme.colors.indicatorActive
            } else {
                FirefoxTheme.colors.indicatorInactive
            },
        )

        Spacer(modifier = Modifier.width(28.dp))

        Indicator(
            color = if (onboardingState == UpgradeOnboardingState.Notifications) {
                FirefoxTheme.colors.indicatorActive
            } else {
                FirefoxTheme.colors.indicatorInactive
            },
        )

        Spacer(modifier = Modifier.width(28.dp))

        Indicator(
            color = if (onboardingState == UpgradeOnboardingState.DefaultBrowser) {
                FirefoxTheme.colors.indicatorActive
            } else {
                FirefoxTheme.colors.indicatorInactive
            },
        )

        Spacer(modifier = Modifier.width(28.dp))

        Indicator(
            color = if (onboardingState == UpgradeOnboardingState.Finish) {
                FirefoxTheme.colors.indicatorActive
            } else {
                FirefoxTheme.colors.indicatorInactive
            },
        )

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
