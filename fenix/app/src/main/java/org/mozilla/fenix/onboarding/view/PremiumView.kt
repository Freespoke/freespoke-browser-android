package org.mozilla.fenix.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.button.PrimaryButtonOnboarding
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
fun PremiumView(
    onDismiss: () -> Unit,
    updatedOnboardingState: (UpgradeOnboardingState) -> Unit,
    pageState: OnboardingPageState,
    modifier: Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        createCloseIcon(modifier = Modifier.align(Alignment.End), onDismiss)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 45.dp),
            text = pageState.title,
            color = FirefoxTheme.colors.onboardingTextColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.headLine3,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 30.dp),
            text = pageState.description,
            color = FirefoxTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.body1,
        )

        Spacer(modifier = Modifier.height(78.dp))

        if (pageState.image != null) {
            Image(
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp),
                painter = painterResource(id = pageState.image),
                contentDescription = null,
                contentScale = ContentScale.Inside,
            )

            LaunchedEffect(pageState) {
                pageState.onRecordImpressionEvent()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(horizontal = 30.dp),
            text = stringResource(id = R.string.premium_badge_example),
            color = FirefoxTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.body1,
        )

    }

    Column(modifier = modifier
        .fillMaxWidth()
        .background(FirefoxTheme.colors.layerOnboarding)
        .shadow(2.dp)
        .padding(vertical = 40.dp, horizontal = 30.dp)
    ) {
        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth(),
            text = pageState.primaryButtonText,
            onClick = {
                updatedOnboardingState(UpgradeOnboardingState.DefaultBrowser)
            },
        )
    }
}
