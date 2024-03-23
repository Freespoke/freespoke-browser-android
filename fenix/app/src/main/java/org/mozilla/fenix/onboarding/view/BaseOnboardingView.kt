package org.mozilla.fenix.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.compose.button.PrimaryButtonOnboarding
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
fun BaseOnboardingView(
    onDismiss: () -> Unit,
    pageState: OnboardingPageState,
    modifier: Modifier,
    updatedOnboardingState: (UpgradeOnboardingState) -> Unit ) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        createCloseIcon(modifier = Modifier.align(Alignment.End), onDismiss)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            modifier = Modifier.padding(horizontal = 45.dp),
            text = pageState.title,
            color = FirefoxTheme.colors.onboardingTextColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.onboardingHeadLine2,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = Modifier.padding(horizontal = 30.dp),
            text = pageState.description,
            color = FirefoxTheme.colors.freespokeDescriptionColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.body1,
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (pageState.image != null) {
            val paddingModifier = if (pageState.type == UpgradeOnboardingState.CompleteOnboarding) {
                Modifier
            } else {
                Modifier.padding(horizontal = 40.dp)
            }

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(paddingModifier),
                painter = painterResource(id = pageState.image),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )

            LaunchedEffect(pageState) {
                pageState.onRecordImpressionEvent()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp)
            .background(FirefoxTheme.colors.layerOnboarding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Divider(modifier = Modifier.fillMaxWidth(),
            color = FirefoxTheme.colors.dividerColor)

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            text = pageState.primaryButtonText,
            onClick = {
                if (pageState.type == UpgradeOnboardingState.CompleteOnboarding) {
                    onDismiss()
                } else {
                    updatedOnboardingState(
                        when(pageState.type) {
                            UpgradeOnboardingState.DefaultBrowser, UpgradeOnboardingState.DefaultBrowserShow -> UpgradeOnboardingState.DefaultBrowserShow
                            UpgradeOnboardingState.Notifications, UpgradeOnboardingState.NotificationsSetup -> UpgradeOnboardingState.NotificationsSetup
                            else -> UpgradeOnboardingState.CompleteOnboarding
                        }
                    )
                }
            },
        )


        if (!pageState.secondaryButtonText.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .clickable {
                        updatedOnboardingState(
                            when (pageState.type) {
                                UpgradeOnboardingState.DefaultBrowser, UpgradeOnboardingState.DefaultBrowserShow -> UpgradeOnboardingState.Notifications
                                else -> UpgradeOnboardingState.CompleteOnboarding
                            }
                        )
                    },
                text = pageState.secondaryButtonText,
                color = FirefoxTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                style = FirefoxTheme.typography.subtitle1,
            )
        }
    }
}
