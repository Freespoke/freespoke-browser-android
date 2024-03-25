/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokeaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.mozilla.fenix.R
import org.mozilla.fenix.freespokeaccount.profile.ProfileBubble
import org.mozilla.fenix.onboarding.view.UpgradeOnboardingState
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
fun FreespokeProfilePage(
    viewModel: FreespokeProfileViewModel = viewModel(factory = FreespokeProfileViewModel.Factory),
    onManageAccount: () -> Unit,
    onManagePremium: () -> Unit,
    onSetDefaultBrowser: () -> Unit,
    onManageNotifications: () -> Unit,
    onShare: () -> Unit,
    onSupport: () -> Unit,
    onManageDarkMode: () -> Unit,
    onBack: () -> Unit,
    onLogout: (onLogoutSuccess: (Boolean) -> Unit) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        FirefoxTheme.colors.freeSpokeProfileGradientStartColor,
                        FirefoxTheme.colors.freeSpokeProfileGradientEndColor,
                    ),
                ),
            )
            .verticalScroll(scrollState)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val profile by viewModel.profileData.collectAsState()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = onBack
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.rotate(180f),
                    painter = painterResource(id = R.drawable.ic_arrowhead_right),
                    contentDescription = null,
                )
                Text(
                    text = stringResource(id = R.string.browser_menu_back),
                    color = FirefoxTheme.colors.freespokeDescriptionColor,
                    style = FirefoxTheme.typography.headline7.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

            ProfileBubble(
                profileUiModel = profile,
                onClick = {},
                isEnabled = false
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.freefolk_profile).uppercase(),
            color = FirefoxTheme.colors.freespokeDescriptionColor,
            style = FirefoxTheme.typography.headline6.copy(
                fontWeight = FontWeight.Bold
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FreespokeProfileListItem(
                type = FreespokeProfileListItemType.Default {
                    onManagePremium()
                },
                text = stringResource(id = R.string.list_item_premium),
                iconPainter = painterResource(id = R.drawable.ic_premium_small),
            )
            FreespokeProfileListItem(
                type = FreespokeProfileListItemType.ExternalLink {
                    onManageAccount()
                },
                text = stringResource(id = R.string.list_item_account),
                iconPainter = painterResource(id = R.drawable.ic_freespoke_flame),
            )
            FreespokeProfileListItem(
                type = FreespokeProfileListItemType.Default {
                    onManageDarkMode()
                },
                text = stringResource(id = R.string.list_item_dark_mode),
                iconPainter = painterResource(id = R.drawable.ic_settings_drawer),
            )
            FreespokeProfileListItem(
                type = FreespokeProfileListItemType.Default {
                    onSetDefaultBrowser()
                },
                text = stringResource(id = R.string.list_item_default_browser),
                iconPainter = painterResource(id = R.drawable.ic_add_default),
            )
            FreespokeProfileListItem(
                type = FreespokeProfileListItemType.Default {
                    onManageNotifications()
                },
                text = stringResource(id = R.string.list_item_notifications),
                iconPainter = painterResource(id = R.drawable.ic_manage_notifications),
            )
            FreespokeProfileListItem(
                type = FreespokeProfileListItemType.Default {
                    onSupport()
                },
                text = stringResource(id = R.string.get_in_touch),
                iconPainter = painterResource(id = R.drawable.ic_chat),
            )
            FreespokeProfileListItem(
                type = FreespokeProfileListItemType.Default {
                    onShare()
                },
                text = stringResource(id = R.string.share_freespoke),
                iconPainter = painterResource(id = R.drawable.ic_share_drawer),
            )

            Spacer(Modifier.height(32.dp))

            profile?.let {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogout { if (it) viewModel.onLogout() } },
                    text = stringResource(id = R.string.log_out),
                    color = FirefoxTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                    style = FirefoxTheme.typography.subtitle1,
                )
            }
        }
    }
}
