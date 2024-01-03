/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onesignal.OneSignal
import mozilla.components.ui.colors.PhotonColors
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.button.PrimaryButtonOnboarding
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
    onPrimaryButtonClick: (Triple<Boolean, Boolean, Boolean>?) -> Unit,
    onSecondaryButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var updates by remember { mutableStateOf(Triple(true, true, true)) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.layerOnboarding)
            .verticalScroll(rememberScrollState())
            .then(modifier)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(280.dp))

            Image(
                modifier = Modifier.height(400.dp),
                painter = painterResource(id = R.drawable.iceberg_hompage_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.mozac_ic_close),
                    contentDescription = stringResource(R.string.onboarding_home_content_description_close_button),
                    tint = colors.iconPrimary,
                )
            }

            Text(
                modifier = Modifier.padding(horizontal = 30.dp),
                text = pageState.title,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                style = if (pageState.type == UpgradeOnboardingState.Welcome) {
                        FirefoxTheme.typography.headline4
                    } else {
                        FirefoxTheme.typography.headline5
                    },
            )

            Spacer(modifier = Modifier.height(
                if (pageState.type == UpgradeOnboardingState.Welcome) {
                    36.dp
                } else {
                    12.dp
                }))

            if (!pageState.description.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.padding(start = 42.dp, end = 42.dp),
                    text = pageState.description,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    style = FirefoxTheme.typography.body1,
                )

                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(50.dp))
            }

            if (pageState.image != null) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(),
                    painter = painterResource(id = pageState.image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            } else if(pageState.type == UpgradeOnboardingState.Updates) {
                Spacer(modifier = Modifier.height(16.dp))
                NotificationUpdatesView(
                    icon = R.drawable.ic_news,
                    title = R.string.breaking_news,
                    description = R.string.breaking_news_frequency,
                    updates.first,
                    onCheckChangeListener = {
                        updates = Triple(it, updates.second, updates.third)
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
                NotificationUpdatesView(
                    icon = R.drawable.ic_shop,
                    title = R.string.shop_usa_deals,
                    description = R.string.shop_deals_frequency,
                    updates.second,
                    onCheckChangeListener = {
                        updates = Triple(updates.first, it, updates.third)
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
                NotificationUpdatesView(
                    icon = R.drawable.ic_general_alert,
                    title = R.string.general_alerts,
                    description = R.string.general_alerts_frequency,
                    updates.third,
                    onCheckChangeListener = {
                        updates = Triple(updates.first, updates.second, it)
                    },
                )
            } else {
                Spacer(modifier = Modifier.height(220.dp))
            }

            LaunchedEffect(pageState) {
                pageState.onRecordImpressionEvent()
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(colors.layerOnboarding)
            .padding(24.dp)
            .align(Alignment.BottomCenter)) {
            PrimaryButtonOnboarding(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                text = pageState.primaryButtonText,
                onClick = {
                    onPrimaryButtonClick(updates)
                },
            )

            if (!pageState.secondaryButtonText.isNullOrEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                        .align(Alignment.TopCenter),
                ) {
                    val textColor = colors.textPrimary
                    Text(
                        modifier = Modifier.drawBehind {
                            val strokeWidthPx = 1.dp.toPx()
                            val verticalOffset = size.height - 2.sp.toPx()
                            drawLine(
                                color =  textColor,
                                strokeWidth = strokeWidthPx,
                                start = Offset(0f, verticalOffset),
                                end = Offset(size.width, verticalOffset)
                            )
                        }
                            .clickable { onSecondaryButtonClick?.invoke() },
                        text = pageState.secondaryButtonText,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        style = FirefoxTheme.typography.subtitle1,
                    )
                }
            }
        }

    }
}

@Composable
private fun NotificationUpdatesView(icon: Int,
                                    title: Int,
                                    description: Int,
                                    isChecked: Boolean,
                                    onCheckChangeListener: (Boolean) -> Unit) {
    Row (modifier = Modifier
        .padding(horizontal = 16.dp)
        .border(border = BorderStroke(width = 1.dp, color = colors.borderPrimary), shape = RoundedCornerShape(12.dp))
        .background(color = colors.layerOnboarding, shape = RoundedCornerShape(12.dp))
        .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
        ){
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = PhotonColors.FreeSpokeButtonColor)
        
        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.width(228.dp),
            horizontalAlignment = Alignment.Start) {
            Text(
                text = stringResource(id = title),
                color = colors.onboardingTextColor  ,
                textAlign = TextAlign.Start,
                style = FirefoxTheme.typography.headline6,
            )
            Text(
                text = stringResource(id = description),
                color = colors.freespokeDescriptionColor  ,
                textAlign = TextAlign.Start,
                style = FirefoxTheme.typography.headline8,
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Switch(
            modifier = Modifier.width(42.dp),
            checked = isChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PhotonColors.FreeSpokeButtonColor,
                uncheckedThumbColor = PhotonColors.FreeSpokeButtonColor,
                uncheckedTrackColor = PhotonColors.FreeSpokeButtonColor,
                uncheckedTrackAlpha = 0.5f),
            onCheckedChange = { isChecked ->
                onCheckChangeListener.invoke(isChecked)
            })
    }
}

@Preview
@Composable
private fun OnboardingPagePreview() {
    FirefoxTheme {
        OnboardingPage(
            pageState = OnboardingPageState(
                type = UpgradeOnboardingState.Welcome,
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
            ) {},
            onPrimaryButtonClick = {},
            onDismiss = {},
        )
    }
}
