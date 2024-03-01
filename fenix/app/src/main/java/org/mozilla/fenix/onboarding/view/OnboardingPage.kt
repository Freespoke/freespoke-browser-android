/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.view

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.button.PrimaryButtonOnboarding
import org.mozilla.fenix.onboarding.AccountViewModel
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
 * @param onPrimaryButtonClick Invoked when the user clicks the primary button.
 * @param onSecondaryButtonClick Invoked when the user clicks the secondary button.
 * @param modifier The modifier to be applied to the Composable.
 */
@Composable
fun OnboardingPage(
    pageState: OnboardingPageState,
    onDismiss: () -> Unit,
    onPrimaryButtonClick: (UpgradeOnboardingState) -> Unit,
    viewModel: AccountViewModel? = null
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.layerOnboarding)
    ) {

            when (pageState.type) {
                UpgradeOnboardingState.Welcome -> createWelcomeView(onDismiss, onPrimaryButtonClick, Modifier.align(Alignment.BottomCenter))
                UpgradeOnboardingState.Registration -> createSignUpView(
                    onDismiss,
                    onPrimaryButtonClick,
                    viewModel
                )

                UpgradeOnboardingState.Subscriptions -> createSubscriptionsView(
                    onDismiss,
                    onPrimaryButtonClick,
                    Modifier.align(Alignment.BottomCenter)
                )

                UpgradeOnboardingState.Login -> createLoginView()
                UpgradeOnboardingState.Premium -> createPremiumView(
                    onDismiss,
                    onPrimaryButtonClick,
                    pageState,
                    Modifier.align(Alignment.BottomCenter)
                )

                else -> createBaseViewWithImageAndButton(
                    onDismiss,
                    pageState,
                    Modifier.align(Alignment.BottomCenter),
                    onPrimaryButtonClick
                )

            }
    }
}

@Composable
fun createPremiumView(
    onDismiss: () -> Unit,
    onPrimaryButtonClick: (UpgradeOnboardingState) -> Unit,
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
            color = colors.onboardingTextColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.headLine3,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 30.dp),
            text = pageState.description,
            color = colors.textSecondary,
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
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.body1,
        )

    }

    Column(modifier = modifier
        .fillMaxWidth()
        .background(colors.layerOnboarding)
        .shadow(2.dp)
        .padding(vertical = 40.dp, horizontal = 30.dp)
    ) {
        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth(),
            text = pageState.primaryButtonText,
            onClick = {
                onPrimaryButtonClick(UpgradeOnboardingState.DefaultBrowser)
            },
        )
    }
}

@Composable
private fun createCloseIcon(modifier: Modifier, onDismiss: () -> Unit) {
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
fun createSubscriptionsView(
    onDismiss: () -> Unit,
    onPrimaryButtonClick: (UpgradeOnboardingState) -> Unit,
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
            color = colors.onboardingTextColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.headLine3,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Divider(modifier = Modifier.fillMaxWidth(),
            color = colors.dividerColor)

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
                    color = colors.freespokeDescriptionColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.headline5,
                )
                Text(
                    text = stringResource(id = R.string.ad_free_search_description),
                    color = colors.subtitleColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.body2,
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth(),
            color = colors.dividerColor)

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
                    color = colors.freespokeDescriptionColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.headline5,
                )
                Text(
                    text = stringResource(id = R.string.without_bias_description),
                    color = colors.subtitleColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.body2,
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth(),
            color = colors.dividerColor)

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
                    color = colors.freespokeDescriptionColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.headline5,
                )
                Text(
                    text = stringResource(id = R.string.porn_free_description),
                    color = colors.subtitleColor,
                    textAlign = TextAlign.Start,
                    style = FirefoxTheme.typography.body2,
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth(),
            color = colors.dividerColor)
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
            color = colors.dividerColor)

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            text = stringResource(id = R.string.subscriptions_rules),
            color = colors.freespokeDescriptionColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.subtitle2,)
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            text = stringResource(id = R.string.month_subscription_value),
            onClick = {
                onPrimaryButtonClick(UpgradeOnboardingState.Premium)
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(id = R.string.year_subscription_value),
            onClick = {
                onPrimaryButtonClick(UpgradeOnboardingState.Premium)
            },
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { },
            text = stringResource(id = R.string.restore_purchase),
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.subtitle1,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPrimaryButtonClick(UpgradeOnboardingState.DefaultBrowser) },
            text = stringResource(id = R.string.continue_without_premium),
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.subtitle1,
        )
    }
}

@Composable
fun createBaseViewWithImageAndButton(
    onDismiss: () -> Unit,
    pageState: OnboardingPageState,
    modifier: Modifier,
    onPrimaryButtonClick: (UpgradeOnboardingState) -> Unit ) {
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
                color = colors.onboardingTextColor,
                textAlign = TextAlign.Center,
                style = FirefoxTheme.typography.headLine3,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                modifier = Modifier.padding(horizontal = 30.dp),
                text = pageState.description,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                style = FirefoxTheme.typography.body1,
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (pageState.image != null) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(),
                    painter = painterResource(id = pageState.image),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                )

                LaunchedEffect(pageState) {
                    pageState.onRecordImpressionEvent()
                }
            }
        }

        Column(modifier = modifier
            .fillMaxWidth()
            .background(colors.layerOnboarding)
        ) {
            Divider(modifier = Modifier.fillMaxWidth(),
                color = colors.dividerColor)

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButtonOnboarding(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                text = pageState.primaryButtonText,
                onClick = {
                    onPrimaryButtonClick(
                        when(pageState.type) {
                            UpgradeOnboardingState.DefaultBrowser, UpgradeOnboardingState.DefaultBrowserShow -> UpgradeOnboardingState.DefaultBrowserShow
                            UpgradeOnboardingState.Notifications, UpgradeOnboardingState.NotificationsSetup -> UpgradeOnboardingState.NotificationsSetup
                            else -> UpgradeOnboardingState.CompleteOnboarding
                        }
                    )
                },
            )


            if (!pageState.secondaryButtonText.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .clickable {
                            onPrimaryButtonClick(
                                when (pageState.type) {
                                    UpgradeOnboardingState.DefaultBrowser, UpgradeOnboardingState.DefaultBrowserShow -> UpgradeOnboardingState.Notifications
                                    else -> UpgradeOnboardingState.CompleteOnboarding
                                }
                            )
                        },
                    text = pageState.secondaryButtonText,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    style = FirefoxTheme.typography.subtitle1,
                )
            }
        }
}

@Composable
fun createSignUpView(onDismiss: () -> Unit,
                     onPrimaryButtonClick: (UpgradeOnboardingState) -> Unit,
                     viewModel: AccountViewModel?) {

        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.layerOnboarding)
                .verticalScroll(rememberScrollState(), true),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            createCloseIcon(modifier = Modifier.align(Alignment.End), onDismiss)

            Spacer(modifier = Modifier.height(10.dp))

            addFreespokeLogo()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 40.dp),
                text = stringResource(id = R.string.join_freespoke_revolution),
                color = colors.onboardingTextColor,
                textAlign = TextAlign.Center,
                style = FirefoxTheme.typography.headLine3,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 40.dp),
                text = stringResource(id = R.string.signup_subtitle),
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                style = FirefoxTheme.typography.body1,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Divider(modifier = Modifier.fillMaxWidth(),
                color = colors.dividerColor)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                modifier = Modifier,
                shape = RoundedCornerShape(8.dp),
                value = firstName,
                onValueChange = {
                    firstName = it
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = colors.layer2),
                label = { Text(stringResource(id = R.string.addresses_first_name)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "",
                        tint = colors.iconPrimary,
                    )
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = colors.layer2),
                label = { Text(stringResource(id = R.string.addresses_last_name)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "",
                        tint = colors.iconPrimary,
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = { Text(stringResource(id = R.string.addresses_email)) },
                colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = colors.layer2),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "",
                        tint = colors.iconPrimary,
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = colors.layer2),
                label = { Text(stringResource(id = R.string.password)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_password),
                        contentDescription = "",
                        tint = colors.iconPrimary,
                    )
                }
            )
            /*
            LaunchedEffect(pageState) {
                pageState.onRecordImpressionEvent()
            }*/
            Divider(modifier = Modifier.fillMaxWidth(),
                color = colors.dividerColor)

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButtonOnboarding(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                text = stringResource(id = R.string.onboarding_next_button),
                onClick = {
                    viewModel?.signUp(firstName, lastName, email, password)
                },
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .clickable { onPrimaryButtonClick(UpgradeOnboardingState.Login) },
                text = stringResource(id = R.string.login_with_account),
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                style = FirefoxTheme.typography.subtitle1,
            )
        }/*

        Column(modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp)
            .background(colors.layerOnboarding)
        ) {

        }*/
}

@Composable
fun createLoginView() {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean {
                        // log click event
                        return super.shouldOverrideUrlLoading(view, request)
                    }
                }

                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
            }
        },
        update = { webView ->
            webView.loadUrl("https://auth.staging.freespoke.com/realms/freespoke-staging")
        }
    )

}

@Composable
fun createWelcomeView(onDismiss: () -> Unit, onActionButtonClick: (UpgradeOnboardingState) -> Unit, modifier: Modifier) {
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
            modifier = Modifier.padding(horizontal = 45.dp),
            text = stringResource(id = R.string.onboarding_home_welcome_new_title),
            color = colors.onboardingTextColor,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.headLine2,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 30.dp),
            text = stringResource(id = R.string.onboarding_home_welcome_subtitle),
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.body1,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier.padding(horizontal = 30.dp),
            text = stringResource(id = R.string.onboarding_home_welcome_subtitle2),
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
        )

    }
            /*
            LaunchedEffect(pageState) {
                pageState.onRecordImpressionEvent()
            }*/


    Column(modifier = modifier
        .fillMaxWidth()
        .height(260.dp)
        .background(colors.layerOnboarding)
    ) {
        Divider(modifier = Modifier.fillMaxWidth(), color = colors.dividerColor)
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            text = stringResource(id = R.string.continue_without_account),
            onClick = {
                onActionButtonClick(UpgradeOnboardingState.DefaultBrowser)
            },
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButtonOnboarding(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            text = stringResource(id = R.string.create_account),
            onClick = {
                onActionButtonClick(UpgradeOnboardingState.Registration)
            },
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onActionButtonClick(UpgradeOnboardingState.Login) },
            text = stringResource(id = R.string.login_with_account),
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            style = FirefoxTheme.typography.subtitle1,
        )
    }
}

@Composable
private fun addFreespokeLogo() {
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
            onPrimaryButtonClick = {},
            viewModel = null,
        )
    }
}
