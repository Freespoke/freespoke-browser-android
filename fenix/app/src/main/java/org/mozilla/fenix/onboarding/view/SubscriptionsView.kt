package org.mozilla.fenix.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.mozilla.fenix.R
import org.mozilla.fenix.components.components
import org.mozilla.fenix.compose.button.PrimaryButtonOnboarding
import org.mozilla.fenix.compose.button.SecondaryButtonOnboarding
import org.mozilla.fenix.ext.asActivity
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.freespokeaccount.store.FreespokeProfileStore
import org.mozilla.fenix.freespokepremium.FreespokePremiumViewModel
import org.mozilla.fenix.freespokepremium.SubscriptionsUiModel
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.theme.FirefoxTheme.colors

@Composable
fun SubscriptionsView(
    onDismiss: () -> Unit,
    updatedOnboardingState: (UpgradeOnboardingState) -> Unit,
    modifier: Modifier,
    onUpgradePlan: (() -> Unit)? = null,
    onCancelPlan: ((Boolean) -> Unit)? = null,
) {

    val activity = LocalContext.current.asActivity()
    val viewModel: FreespokePremiumViewModel = viewModel(
        factory = FreespokePremiumViewModel.Factory
    )

    val pageType by viewModel.uiTypeFlow.collectAsState()
    val subscriptions by viewModel.subscriptionUiStateFlow.collectAsState()

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

    pageType?.let {
        SubscriptionInfoBlock(
            modifier = modifier,
            subscriptionsUiModel = subscriptions,
            type = it,
            updatedOnboardingState = updatedOnboardingState,
            onUpgradePlan = onUpgradePlan,
            onCancelPlan = onCancelPlan,
            onLaunchPurchaseFlow = { offerToken ->
                activity?.let { activity ->
                    viewModel.launchPurchaseFlow(activity, offerToken) {
                        updatedOnboardingState(UpgradeOnboardingState.Premium)
                    }
                }
            }
        )
    }
}

enum class SubscriptionInfoBlockType {
    Trial,
    Regular,
    Upgrade,
    Cancel
}

@Composable
fun SubscriptionInfoBlock(
    modifier: Modifier,
    subscriptionsUiModel: SubscriptionsUiModel?,
    type: SubscriptionInfoBlockType,
    updatedOnboardingState: (UpgradeOnboardingState) -> Unit,
    onLaunchPurchaseFlow: (String) -> Unit,
    onUpgradePlan: (() -> Unit)? = null,
    onCancelPlan: ((Boolean) -> Unit)? = null,
) {

    subscriptionsUiModel?.let { uiModel ->
        Column(modifier = modifier
            .fillMaxWidth()
            .background(colors.layerOnboarding)
            .padding(bottom = 32.dp)
        ) {
            Divider(modifier = Modifier.fillMaxWidth(),
                color = FirefoxTheme.colors.dividerColor)

            Spacer(modifier = Modifier.height(32.dp))
            if (type == SubscriptionInfoBlockType.Trial) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    text = stringResource(id = R.string.subscriptions_rules),
                    color = FirefoxTheme.colors.freespokeDescriptionColor,
                    textAlign = TextAlign.Center,
                    style = FirefoxTheme.typography.subtitle2)
                Spacer(modifier = Modifier.height(32.dp))
            }

            SubscriptionInfoButtons(
                type = type,
                uiModel = uiModel,
                onPurchasePlan = { offerToken ->
                    onLaunchPurchaseFlow(offerToken)
                },
                onUpgradePlan = {
                    onUpgradePlan?.invoke()
                },
                onCancelPlan = {
                    onCancelPlan?.invoke(type == SubscriptionInfoBlockType.Upgrade)
                }
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

            val continueActionText = when(type) {
                SubscriptionInfoBlockType.Trial, SubscriptionInfoBlockType.Regular -> {
                    stringResource(id = R.string.continue_without_premium)
                }
                else -> {
                    stringResource(id = R.string.continue_without_updating)
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { updatedOnboardingState(UpgradeOnboardingState.DefaultBrowser) },
                text = continueActionText,
                color = FirefoxTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                style = FirefoxTheme.typography.subtitle1,
            )
        }
    }
}

@Composable
fun ColumnScope.SubscriptionInfoButtons(
    type: SubscriptionInfoBlockType,
    uiModel: SubscriptionsUiModel,
    onPurchasePlan: (String) -> Unit,
    onUpgradePlan: () -> Unit,
    onCancelPlan: () -> Unit,
) {

    val primaryButtonText: String? = when(type) {
        SubscriptionInfoBlockType.Trial, SubscriptionInfoBlockType.Regular -> {
            stringResource(id = R.string.month_subscription_value, uiModel.monthlyPrice)
        }
        SubscriptionInfoBlockType.Upgrade -> {
            stringResource(id = R.string.update_plan)
        }
        else -> null
    }

    val secondaryButtonText: String = when(type) {
        SubscriptionInfoBlockType.Trial, SubscriptionInfoBlockType.Regular -> {
            stringResource(id = R.string.year_subscription_value, uiModel.yearlyPrice)
        }
        SubscriptionInfoBlockType.Upgrade, SubscriptionInfoBlockType.Cancel -> {
            stringResource(id = R.string.cancel_plan)
        }
    }

    primaryButtonText?.let {
        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            text = primaryButtonText,
            onClick = {
                when(type) {
                    SubscriptionInfoBlockType.Trial, SubscriptionInfoBlockType.Regular -> {
                        onPurchasePlan(uiModel.monthlyOfferToken)
                    }
                    else -> onUpgradePlan()
                }
            },
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
    when(type) {
        SubscriptionInfoBlockType.Trial, SubscriptionInfoBlockType.Regular -> {
            PrimaryButtonOnboarding(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                text = secondaryButtonText,
                onClick = {
                    when(type) {
                        SubscriptionInfoBlockType.Trial, SubscriptionInfoBlockType.Regular -> {
                            onPurchasePlan(uiModel.yearlyOfferToken)
                        }
                        else -> onUpgradePlan()
                    }
                },
            )
        }
        else -> {
            SecondaryButtonOnboarding(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                text = secondaryButtonText,
                onClick = {
                    onCancelPlan()
                },
            )
        }
    }
}
