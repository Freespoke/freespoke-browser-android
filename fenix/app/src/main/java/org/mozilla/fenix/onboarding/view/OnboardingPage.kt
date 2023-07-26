/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.annotation.LightDarkPreview
import org.mozilla.fenix.compose.button.PrimaryButtonOnboarding
import org.mozilla.fenix.theme.FirefoxTheme

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
 * @param onPrimaryButtonClick Invoked when the user clicks the primary button.
 * @param onSecondaryButtonClick Invoked when the user clicks the secondary button.
 * @param modifier The modifier to be applied to the Composable.
 */
@Composable
fun OnboardingPage(
    pageState: OnboardingPageState,
    onDismiss: () -> Unit,
    onPrimaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = Modifier
            .background(FirefoxTheme.colors.layerOnboarding)
            .padding(bottom = if (pageState.secondaryButtonText == null) 32.dp else 24.dp)
            .then(modifier),
    ) {
        val isVisible = pageState.title.isNotEmpty()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            if (isVisible) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.mozac_ic_close),
                        contentDescription = stringResource(R.string.onboarding_home_content_description_close_button),
                        tint = FirefoxTheme.colors.iconPrimary,
                    )
                }


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(modifier = Modifier.padding(start = 42.dp, end = 42.dp)) {
                        Text(
                            text = pageState.title,
                            color = FirefoxTheme.colors.textPrimary,
                            textAlign = TextAlign.Center,
                            style = FirefoxTheme.typography.headline4,
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(modifier = Modifier.padding(start = 42.dp, end = 42.dp)) {
                        Text(
                            text = pageState.description,
                            color = FirefoxTheme.colors.textSecondary,
                            textAlign = TextAlign.Center,
                            style = FirefoxTheme.typography.body1,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (pageState.image != null) {
                        Image(
                            painter = painterResource(id = pageState.image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                        )
                    }

                }
            }

            if (isVisible.not()) {
                Image(
                    painter = painterResource(id = pageState.image!!),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 42.dp),
            ) {
                PrimaryButtonOnboarding(
                    text = stringResource(id = R.string.onboarding_next_button),
                    onClick = onPrimaryButtonClick,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.search_better_browse_better),
                    color = FirefoxTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                    style = FirefoxTheme.typography.subtitle3,
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stringResource(id = R.string.set_freespoke_as_your_default_browser),
                    textDecoration = TextDecoration.Underline,
                    color = FirefoxTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    style = FirefoxTheme.typography.subtitle1,
                )
            }

            LaunchedEffect(pageState) {
                pageState.onRecordImpressionEvent()
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun OnboardingPagePreview() {
    FirefoxTheme {
        OnboardingPage(
            pageState = OnboardingPageState(
                image = null,
                title = stringResource(
                    id = R.string.onboarding_home_enable_notifications_title,
                    formatArgs = arrayOf(stringResource(R.string.app_name)),
                ),
                description = stringResource(
                    id = R.string.onboarding_home_enable_notifications_description,
                    formatArgs = arrayOf(stringResource(R.string.app_name)),
                ),
                primaryButtonText = stringResource(
                    id = R.string.onboarding_home_enable_notifications_positive_button,
                ),
                secondaryButtonText = stringResource(
                    id = R.string.onboarding_home_enable_notifications_negative_button,
                ),
                onRecordImpressionEvent = {},
            ),
            onPrimaryButtonClick = {},
            onDismiss = {},
        )
    }
}
