/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokeaccount

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mozilla.fenix.R
import org.mozilla.fenix.theme.FirefoxTheme

@Suppress("DEPRECATION")
@Composable
fun FreespokeProfileListItem(
    type: FreespokeProfileListItemType,
    text: String,
    iconPainter: Painter,
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val clickableModifier = when (type) {
        is FreespokeProfileListItemType.Default -> {
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    type.onClick()
                },
            )
        }

        is FreespokeProfileListItemType.ExternalLink -> {
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    type.onClick()
                },
            )
        }

        is FreespokeProfileListItemType.Toggle -> {
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    type.onToggled(false)
                },
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, FirefoxTheme.colors.freespokeBorderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(6.dp))
            .then(clickableModifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Image(
            painter = iconPainter,
            contentDescription = null,
        )


        Text(
            modifier = Modifier.weight(1f),
            text = text,
            color = FirefoxTheme.colors.freespokeDescriptionColor,
            textAlign = TextAlign.Start,
            maxLines = 1,
            lineHeight = 19.sp,
            style = FirefoxTheme.typography.body1.copy(
                fontSize = 19.sp,
                fontWeight = FontWeight.W600,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false,
                ),
            ),
        )


        when (type) {
            is FreespokeProfileListItemType.ExternalLink -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrowhead_right),
                    contentDescription = null,
                )
            }

            is FreespokeProfileListItemType.Toggle -> {
                Switch(
                    checked = type.isToggled,
                    onCheckedChange = null,
                )
            }

            else -> {}
        }
    }
}

sealed class FreespokeProfileListItemType {
    data class ExternalLink(
        val onClick: () -> Unit,
    ) : FreespokeProfileListItemType()

    data class Toggle(
        val isToggled: Boolean,
        val onToggled: (Boolean) -> Unit,
    ) : FreespokeProfileListItemType()

    data class Default(
        val onClick: () -> Unit,
    ) : FreespokeProfileListItemType()
}
