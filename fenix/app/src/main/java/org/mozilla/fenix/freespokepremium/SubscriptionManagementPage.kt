/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokepremium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mozilla.telemetry.glean.private.NoExtras
import org.mozilla.fenix.GleanMetrics.Onboarding
import org.mozilla.fenix.onboarding.view.LoginView
import org.mozilla.fenix.onboarding.view.OnboardingPageState
import org.mozilla.fenix.onboarding.view.PremiumView
import org.mozilla.fenix.onboarding.view.SubscriptionInfoBlockType
import org.mozilla.fenix.onboarding.view.SubscriptionsView
import org.mozilla.fenix.onboarding.view.UpgradeOnboardingState
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
fun SubscriptionManagementPage(
    onDismiss: () -> Unit,
) {

    var type by remember {
        mutableStateOf(UpgradeOnboardingState.Subscriptions)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(FirefoxTheme.colors.layerOnboarding)
    ) {

        when (type) {
            UpgradeOnboardingState.Subscriptions -> SubscriptionsView(
                subscriptionPageType = SubscriptionInfoBlockType.Regular,
                onDismiss = onDismiss,
                updatedOnboardingState = { type = it },
                modifier = Modifier.align(Alignment.BottomCenter),
                onUpgradePlan = {

                },
                onCancelPlan = {

                }
            )
            UpgradeOnboardingState.Login -> LoginView()
            UpgradeOnboardingState.Premium -> PremiumView(
                onDismiss,
                { type = it },
                OnboardingPageState(
                    type = type,
                    onRecordImpressionEvent = {
                        Onboarding.syncCardImpression.record(NoExtras())
                    },
                ),
                Modifier.align(Alignment.BottomCenter)
            )
            else -> {}
        }
    }
}
