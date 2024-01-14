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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import mozilla.telemetry.glean.private.NoExtras
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.annotation.LightDarkPreview
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.GleanMetrics.Onboarding as OnboardingMetrics

/**
 * Enum that represents the onboarding screen that is displayed.
 */
private enum class UpgradeOnboardingState {
    Welcome,
    Newsfeed,
    Election,
    //Shop,
    Search,
    Tabs,
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
) {
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection()) {
        UpgradeOnboardingContent(
            isSyncSignIn = isSyncSignIn,
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun UpgradeOnboardingContent(
    isSyncSignIn: Boolean,
    onDismiss: () -> Unit,
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
                    image = R.drawable.ic_onboarding_welcome,
                    title = "",
                    description = "",
                    primaryButtonText = stringResource(id = R.string.onboarding_home_get_started_button),
                    onRecordImpressionEvent = {
                        OnboardingMetrics.welcomeCardImpression.record(NoExtras())
                    },
                )
                UpgradeOnboardingState.Newsfeed -> OnboardingPageState(
                    image = R.drawable.ic_onboarding_news,
                    title = stringResource(id = R.string.newsfeed_onboarding),
                    description = stringResource(id = R.string.navigating_the_news_onboarding),
                    primaryButtonText = stringResource(id = R.string.onboarding_home_sign_in_button),
                    secondaryButtonText = stringResource(id = R.string.onboarding_home_skip_button),
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )
                UpgradeOnboardingState.Election -> OnboardingPageState(
                    image = R.drawable.ic_onboarding_elections,
                    title = stringResource(id = R.string.onboarding_elections),
                    description = stringResource(id = R.string.onboarding_election_subtitle),
                    primaryButtonText = stringResource(id = R.string.onboarding_home_sign_in_button),
                    secondaryButtonText = stringResource(id = R.string.onboarding_home_skip_button),
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )
              /*  UpgradeOnboardingState.Shop -> OnboardingPageState(
                    image = R.drawable.ic_onboarding_shop,
                    title = stringResource(id = R.string.onboarding_shop_usa),
                    description = stringResource(id = R.string.onboarding_shop_subtitle),
                    primaryButtonText = stringResource(id = R.string.onboarding_home_sign_in_button),
                    secondaryButtonText = stringResource(id = R.string.onboarding_home_skip_button),
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )*/
                UpgradeOnboardingState.Search -> OnboardingPageState(
                    image = R.drawable.ic_onboarding_search,
                    title = stringResource(id = R.string.onboarding_search),
                    description = stringResource(id = R.string.onboarding_search_subtitle),
                    primaryButtonText = stringResource(id = R.string.onboarding_home_sign_in_button),
                    secondaryButtonText = stringResource(id = R.string.onboarding_home_skip_button),
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )
                UpgradeOnboardingState.Tabs -> OnboardingPageState(
                    image = R.drawable.ic_onboarding_tabs,
                    title = stringResource(id = R.string.onboarding_tabs),
                    description = stringResource(id = R.string.onboarding_tabs_subtitle),
                    primaryButtonText = stringResource(id = R.string.onboarding_home_sign_in_button),
                    secondaryButtonText = stringResource(id = R.string.onboarding_home_skip_button),
                    onRecordImpressionEvent = {
                        OnboardingMetrics.syncCardImpression.record(NoExtras())
                    },
                )
            },
            onDismiss = {
                onDismiss()
            },
            onPrimaryButtonClick = {
                when (onboardingState) {
                    UpgradeOnboardingState.Welcome -> {
                        onboardingState = UpgradeOnboardingState.Newsfeed
                    }
                    UpgradeOnboardingState.Newsfeed -> {
                        onboardingState = UpgradeOnboardingState.Election
                    }
                    UpgradeOnboardingState.Election -> {
                        onboardingState = UpgradeOnboardingState.Search
                    }
                    UpgradeOnboardingState.Search -> {
                        onboardingState = UpgradeOnboardingState.Tabs
                    }
                    UpgradeOnboardingState.Tabs -> {
                        onDismiss()
                    }
                }
            },
            modifier = Modifier.weight(1f),
            contentScale = if (onboardingState == UpgradeOnboardingState.Election) {
                ContentScale.Inside
            } else {
                ContentScale.Crop
            }
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
            color = if (onboardingState == UpgradeOnboardingState.Newsfeed) {
                FirefoxTheme.colors.indicatorActive
            } else {
                FirefoxTheme.colors.indicatorInactive
            },
        )

        Spacer(modifier = Modifier.width(28.dp))

        Indicator(
            color = if (onboardingState == UpgradeOnboardingState.Election) {
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
