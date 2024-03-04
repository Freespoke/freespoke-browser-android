/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.R
import org.mozilla.fenix.onboarding.viewmodel.AccountViewModel
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.theme.FirefoxTheme.colors

/**
 * The ratio of the image height to the window height. This was determined from the designs in figma
 * taking the ratio of the image height to the mockup height.
 */
private const val IMAGE_HEIGHT_RATIO = 0.4f

/**
 * A composable for displaying onboarding page content.
 *
 * @param pageState [OnboardingPageState] The page content that's displayed.
 * @param onDismiss Invoked when the user clicks the close button.
 * @param updatedOnboardingState Invoked when the user clicks the primary button.
 * @param onSecondaryButtonClick Invoked when the user clicks the secondary button.
 * @param modifier The modifier to be applied to the Composable.
 */
@Composable
fun OnboardingPage(
    pageState: OnboardingPageState,
    onDismiss: () -> Unit,
    updatedOnboardingState: (UpgradeOnboardingState) -> Unit,
    viewModel: AccountViewModel?,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.layerOnboarding)
            .then(modifier)
    ) {

        when (pageState.type) {
            UpgradeOnboardingState.Welcome -> WelcomeView(onDismiss, updatedOnboardingState, Modifier.align(Alignment.BottomCenter))
            UpgradeOnboardingState.Registration -> viewModel?.let {
                SignUpView(
                    onDismiss = onDismiss,
                    updatedOnboardingState = updatedOnboardingState,
                    viewModel = it,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            UpgradeOnboardingState.Subscriptions -> SubscriptionsView(
                onDismiss,
                updatedOnboardingState,
                Modifier.align(Alignment.BottomCenter)
            )
            UpgradeOnboardingState.Login -> LoginView()
            UpgradeOnboardingState.Premium -> PremiumView(
                onDismiss,
                updatedOnboardingState,
                pageState,
                Modifier.align(Alignment.BottomCenter)
            )
            else -> BaseOnboardingView(
                onDismiss,
                pageState,
                Modifier.align(Alignment.BottomCenter),
                updatedOnboardingState
            )
        }
    }
}

@Composable
fun createCloseIcon(modifier: Modifier, onDismiss: () -> Unit) {
    Spacer(modifier = Modifier.height(20.dp))

    IconButton(
        onClick = onDismiss,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.mozac_ic_close),
            contentDescription = stringResource(R.string.onboarding_home_content_description_close_button),
            tint = colors.iconPrimary,
        )
    }
}

@Composable
fun addFreespokeLogo() {
    Image(
        painter = painterResource(id = R.drawable.ic_freespoke),
        contentDescription = "",
        modifier = Modifier
            .height(64.dp)
            .width(64.dp),
    )
}

@Preview
@Composable
private fun OnboardingPagePreview() {
    FirefoxTheme {
        OnboardingPage(
            pageState = OnboardingPageState(
                type = UpgradeOnboardingState.Welcome,
                image = R.drawable.ic_premium_big,
                title = stringResource(
                    id = R.string.onboarding_home_enable_notifications_title,
                    formatArgs = arrayOf(stringResource(R.string.freespoke)),
                ),
                description = stringResource(
                    id = R.string.onboarding_home_enable_notifications_description,
                    formatArgs = arrayOf(stringResource(R.string.freespoke)),
                ),
                primaryButtonText = stringResource(
                    id = R.string.onboarding_home_enable_notifications_positive_button,
                ),
                secondaryButtonText = stringResource(
                    id = R.string.onboarding_home_enable_notifications_negative_button,
                ),
            ) {},
            onDismiss = {},
            updatedOnboardingState = {},
            viewModel = null,
        )
    }
}
