package org.mozilla.fenix.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.button.PrimaryButtonOnboarding
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.theme.FirefoxTheme.colors

@Composable
fun SubscriptionsView(
    onDismiss: () -> Unit,
    updatedOnboardingState: (UpgradeOnboardingState) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        createCloseIcon(modifier = Modifier.align(Alignment.End), onDismiss)

        Spacer(modifier = Modifier.height(10.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_freespoke),
            contentDescription = "",
            modifier = Modifier
                .height(64.dp)
                .width(64.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 40.dp),
            text = stringResource(id = R.string.start_free_trial),
            color = FirefoxTheme.colors.onboardingTextColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.headLine3,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Divider(modifier = Modifier.fillMaxWidth(),
            color = FirefoxTheme.colors.dividerColor)

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Image(painter = painterResource(id = R.drawable.ic_free_search), contentDescription = "")
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.ad_free_search),
                    color = FirefoxTheme.colors.freespokeDescriptionColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.headline5,
                )
                Text(
                    text = stringResource(id = R.string.ad_free_search_description),
                    color = FirefoxTheme.colors.subtitleColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.body2,
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth(),
            color = FirefoxTheme.colors.dividerColor)

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Image(painter = painterResource(id = R.drawable.ic_without_bias), contentDescription = "")
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.without_bias),
                    color = FirefoxTheme.colors.freespokeDescriptionColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.headline5,
                )
                Text(
                    text = stringResource(id = R.string.without_bias_description),
                    color = FirefoxTheme.colors.subtitleColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.body2,
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth(),
            color = FirefoxTheme.colors.dividerColor)

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Image(painter = painterResource(id = R.drawable.ic_porn_free), contentDescription = "")
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.porn_free),
                    color = FirefoxTheme.colors.freespokeDescriptionColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.headline5,
                )
                Text(
                    text = stringResource(id = R.string.porn_free_description),
                    color = FirefoxTheme.colors.subtitleColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.body2,
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth(),
            color = FirefoxTheme.colors.dividerColor)
        /*
        LaunchedEffect(pageState) {
            pageState.onRecordImpressionEvent()
        }*/
    }

    Column(modifier = modifier
            .fillMaxWidth()
            .background(colors.layerOnboarding)
            .padding(bottom = 32.dp)
    ) {
        Divider(modifier = Modifier.fillMaxWidth(),
            color = FirefoxTheme.colors.dividerColor)

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            text = stringResource(id = R.string.subscriptions_rules),
            color = FirefoxTheme.colors.freespokeDescriptionColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.subtitle2,)
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            text = stringResource(id = R.string.month_subscription_value),
            onClick = {
                updatedOnboardingState(UpgradeOnboardingState.Premium)
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(id = R.string.year_subscription_value),
            onClick = {
                updatedOnboardingState(UpgradeOnboardingState.Premium)
            },
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { },
            text = stringResource(id = R.string.restore_purchase),
            color = FirefoxTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.subtitle1,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { updatedOnboardingState(UpgradeOnboardingState.DefaultBrowser) },
            text = stringResource(id = R.string.continue_without_premium),
            color = FirefoxTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.subtitle1,
        )
    }
}
