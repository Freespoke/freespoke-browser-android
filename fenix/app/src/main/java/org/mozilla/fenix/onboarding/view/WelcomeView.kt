package org.mozilla.fenix.onboarding.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.button.PrimaryButtonOnboarding
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
fun WelcomeView(onDismiss: () -> Unit, updatedOnboardingState: (UpgradeOnboardingState) -> Unit, modifier: Modifier) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        createCloseIcon(modifier = Modifier.align(Alignment.End), onDismiss)

        Spacer(modifier = Modifier.height(10.dp))

        addFreespokeLogo()

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 40.dp),
            text = stringResource(id = R.string.onboarding_home_welcome_new_title),
            color = FirefoxTheme.colors.onboardingTextColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.onboardingHeadLine1,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 30.dp),
            text = stringResource(id = R.string.onboarding_home_welcome_subtitle),
            color = FirefoxTheme.colors.freespokeDescriptionColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.body1,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            modifier = Modifier.padding(horizontal = 30.dp),
            text = stringResource(id = R.string.onboarding_home_welcome_subtitle2),
            color = FirefoxTheme.colors.freespokeDescriptionColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
        )

    }
    /*
    LaunchedEffect(pageState) {
        pageState.onRecordImpressionEvent()
    }*/


    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(FirefoxTheme.colors.layerOnboarding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Divider(modifier = Modifier.fillMaxWidth(), color = FirefoxTheme.colors.dividerColor)
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            text = stringResource(id = R.string.continue_without_account),
            onClick = {
                updatedOnboardingState(UpgradeOnboardingState.DefaultBrowser)
            },
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            text = stringResource(id = R.string.create_account),
            onClick = {
                updatedOnboardingState(UpgradeOnboardingState.Registration)
            },
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier
                .clickable {
                    updatedOnboardingState(UpgradeOnboardingState.Login)
                },
            text = stringResource(id = R.string.login_with_account),
            color = FirefoxTheme.colors.freespokeDescriptionColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.body1,
        )
    }
}

@Preview
@Composable
fun previewWelcome() {
    WelcomeView(onDismiss = { }, updatedOnboardingState = {}, modifier = Modifier)
}
