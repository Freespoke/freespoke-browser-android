/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokepremium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import mozilla.telemetry.glean.private.NoExtras
import org.mozilla.fenix.GleanMetrics.Onboarding
import org.mozilla.fenix.components.components
import org.mozilla.fenix.onboarding.view.OnboardingPageState
import org.mozilla.fenix.onboarding.view.PremiumView
import org.mozilla.fenix.onboarding.view.SignUpView
import org.mozilla.fenix.onboarding.view.SubscriptionsView
import org.mozilla.fenix.onboarding.view.UpgradeOnboardingState
import org.mozilla.fenix.onboarding.viewmodel.AccountViewModel
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
fun SubscriptionManagementPage(
    onUpgradePlan: () -> Unit,
    onCancelPlan: (Boolean) -> Unit,
    onLogin: (() -> Unit) -> Unit,
    onDismiss: () -> Unit,
) {

    val viewModel: AccountViewModel = viewModel(factory = AccountViewModel.Factory)

    val profileStore = components.freespokeProfileStore
    val initialState = if (profileStore.state.profile != null) {
        UpgradeOnboardingState.Subscriptions
    } else {
        UpgradeOnboardingState.Registration
    }

    var type by remember {
        mutableStateOf(initialState)
    }

    Column(
        modifier = Modifier
            .background(FirefoxTheme.colors.layer1)
            .systemBarsPadding()
            .imePadding()
            .fillMaxSize(),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(FirefoxTheme.colors.layerOnboarding)
                .weight(1f)
        ) {

            when (type) {
                UpgradeOnboardingState.Subscriptions -> SubscriptionsView(
                    onDismiss = onDismiss,
                    updatedOnboardingState = { type = it },
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onUpgradePlan = onUpgradePlan,
                    onCancelPlan = onCancelPlan,
                )

                UpgradeOnboardingState.Premium -> PremiumView(
                    onDismiss,
                    { type = it },
                    OnboardingPageState(
                        type = type,
                        onRecordImpressionEvent = {
                            Onboarding.syncCardImpression.record(NoExtras())
                        },
                    ),
                    Modifier.align(Alignment.BottomCenter),
                )

                UpgradeOnboardingState.Registration -> SignUpView(
                    onDismiss = onDismiss,
                    updatedOnboardingState = {
                        if (it == UpgradeOnboardingState.Login) {
                            onLogin {
                                type = UpgradeOnboardingState.Subscriptions
                            }
                        } else {
                            type = it
                        }
                    },
                    viewModel = viewModel,
                    forcePadding = true,
                )

                else -> {}
            }
        }
    }
}
