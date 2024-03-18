/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokeaccount

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    modifier: Modifier? = null,
    type: FreespokeProfileListItemType,
    text: String,
    iconPainter: Painter,
) {

    val clickableModifier = getClickableModifier(type)

    Row(
        modifier = modifier ?: Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, FirefoxTheme.colors.dividerColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(4.dp))
            .then(clickableModifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                var checked by remember {
                    mutableStateOf(type.isToggled)
                }
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        type.isToggled = it
                        checked = it
                        type.onToggled(it)
                    },
                    enabled = true,
                )
            }

            else -> {}
        }
    }
}

@Composable
fun getClickableModifier(type: FreespokeProfileListItemType): Modifier {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    return when (type) {
        is FreespokeProfileListItemType.Default -> {
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
                    type.onToggled(type.isToggled)
                },
            )
        }

        else -> {
            Modifier
        }
    }
}

@Suppress("DEPRECATION")
@Composable
fun FreespokeProfileListItemWithButton(
    type: FreespokeProfileListItemType,
    text: String,
    iconPainter: Painter,
    buttonText: String,
    expanded: Boolean,
    onButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (expanded) 114.dp else 56.dp)
            .border(1.dp, FirefoxTheme.colors.dividerColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 14.dp)
            .clip(RoundedCornerShape(4.dp)),
    ) {
        FreespokeProfileListItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .then(getClickableModifier(type = type)),
            type = type, text = text, iconPainter = iconPainter,
        )
        if (expanded) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = FirefoxTheme.colors.dividerColor,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = FirefoxTheme.colors.manageWhiteListButtonBackgroundColor),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp),
                onClick = { onButtonClick() },
            ) {
                Text(modifier = Modifier.weight(1f),
                    color = FirefoxTheme.colors.freespokeDescriptionColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    lineHeight = 19.sp,
                    style = FirefoxTheme.typography.body1.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ), text = buttonText)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

sealed class FreespokeProfileListItemType {
    data class ExternalLink(
        val url: String,
    ) : FreespokeProfileListItemType()

    data class Toggle(
        var isToggled: Boolean,
        val onToggled: (Boolean) -> Unit,
    ) : FreespokeProfileListItemType()

    data class Default(
        val onClick: () -> Unit,
    ) : FreespokeProfileListItemType()
}
