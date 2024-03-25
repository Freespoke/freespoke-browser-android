package org.mozilla.fenix.whitelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.button.TextButton
import org.mozilla.fenix.freespokeaccount.FreespokeProfileListItem
import org.mozilla.fenix.freespokeaccount.FreespokeProfileListItemType
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.theme.defaultTypography

@Suppress("DEPRECATION")
@Composable
fun FreespokeWhiteListPage(
    viewModel: FreespokeWhiteListViewModel = viewModel(factory = FreespokeWhiteListViewModel.Factory),
    onAdBlockStatusChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val whiteList = viewModel.whiteListData.collectAsState()
    val adsBlockEnabled by viewModel.adsBlockEnabled.collectAsState()
    val interactionSource = remember {
        MutableInteractionSource()
    }
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
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var onUiVisible by remember { mutableStateOf(adsBlockEnabled) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = onBack,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.rotate(180f),
                painter = painterResource(id = R.drawable.ic_arrowhead_right),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.browser_menu_back),
                color = FirefoxTheme.colors.textPrimary,
                style = FirefoxTheme.typography.headline7.copy(
                    fontWeight = FontWeight.W700,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Normal,
                    lineHeight = 21.sp
                ),
            )
            Image(painter = painterResource(id = R.drawable.ic_premium_badge), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(16.dp))
        FreespokeProfileListItem(
            type = FreespokeProfileListItemType.Toggle(
                adsBlockEnabled,
                onToggled = {
                    viewModel.updateAdsBlockState(it)
                    onAdBlockStatusChange(it)
                    onUiVisible = it
                },
            ),
            text = stringResource(id = R.string.text_block_ads),
            iconPainter = painterResource(id = R.drawable.ic_settings_drawer),
        )
        if (onUiVisible) {
            var alreadyExistItem by remember { mutableStateOf("") }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.text_title_whitelist).uppercase(),
                color = FirefoxTheme.colors.freespokeDescriptionColor,
                style = FirefoxTheme.typography.headline7.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.text_description_whitelist),
                color = FirefoxTheme.colors.freespokeDescriptionColor,
                style = FirefoxTheme.typography.body1.copy(
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            var text by remember { mutableStateOf("") }
            var isEnabled by remember { mutableStateOf(false) }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(0.dp)
                    .background(color = FirefoxTheme.colors.freeSpokeProfileGradientEndColor),
                value = text,
                onValueChange = {
                    isEnabled = it.isNotBlank()
                    text = it
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done,
                ),
                textStyle = FirefoxTheme.typography.body1.copy(
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                ),
                shape = RoundedCornerShape(4.dp),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.text_add_website),
                        color = FirefoxTheme.colors.freespokeDescriptionColor,
                        style = FirefoxTheme.typography.body1.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start,
                        ),
                    )
                },
                label = null,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = FirefoxTheme.colors.freespokeBorderColor,
                    unfocusedBorderColor = FirefoxTheme.colors.freespokeBorderColor,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = FirefoxTheme.colors.onboardingButtonColor),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp),
                onClick = {
                    if (viewModel.addToWhiteList(text).not()) {
                        alreadyExistItem = text
                    }
                    text = ""
                },
                enabled = isEnabled,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    color = FirefoxTheme.colors.textActionPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    lineHeight = 19.sp,
                    style = FirefoxTheme.typography.body1.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                    text = stringResource(id = R.string.text_button_add_whitelist),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (alreadyExistItem.isNotBlank()) {
                ComfirmDialog(
                    dialogTitle = stringResource(id = R.string.text_add_website),
                    dialogDescription = stringResource(
                        id = R.string.text_description_dialog_whitelist_already_exist,
                        alreadyExistItem,
                    ),
                    declineButtonText = stringResource(id = R.string.text_button_ok),
                    onDismissRequest = {
                        alreadyExistItem = ""
                    },
                )
            }
            val dialogItem = remember { mutableStateOf("") }
            if (dialogItem.value.isNotBlank()) {
                ComfirmDialog(
                    dialogTitle = stringResource(id = R.string.text_title_dialog_whitelist_remove),
                    dialogDescription = stringResource(
                        id = R.string.text_description_dialog_whitelist_remove,
                        dialogItem.value,
                    ),
                    allowButtonText = stringResource(id = R.string.text_button_confirm),
                    declineButtonText = stringResource(id = R.string.text_button_dismiss),
                    onAllowRequest = {
                        viewModel.removeItem(dialogItem.value)
                        dialogItem.value = ""
                    },
                    onDismissRequest = {
                        dialogItem.value = ""
                    },
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(whiteList.value.orEmpty()) { item ->
                    WhiteListItem(
                        data = item,
                        modifier = Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                dialogItem.value = item
                            },
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun ComfirmDialog(
    dialogTitle: String,
    dialogDescription: String,
    allowButtonText: String? = null,
    declineButtonText: String,
    onDismissRequest: () -> Unit,
    onAllowRequest: (() -> Unit)? = null,
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .width(270.dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(14.dp))
                .background(color = FirefoxTheme.colors.layer2),
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = FirefoxTheme.colors.textPrimary,
                        text = dialogTitle,
                        style = defaultTypography.headline6.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.W600,
                            lineHeight = 22.sp,
                            letterSpacing = (-0.4).sp,
                            textAlign = TextAlign.Center,
                        ),
                    )
                }
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = FirefoxTheme.colors.textPrimary,
                    fontSize = 13.sp,
                    text = dialogDescription,
                    style = defaultTypography.body1.copy(
                        fontWeight = FontWeight.W400,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        letterSpacing = (-0.08).sp,
                        textAlign = TextAlign.Center,
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(modifier = Modifier.height(1.dp), color = FirefoxTheme.colors.freespokeBorderColor)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        text = declineButtonText,
                        onClick = onDismissRequest,
                        textColor = FirefoxTheme.colors.dialogButtonsBlueTextColor,
                    )
                    onAllowRequest?.let {
                        Divider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(44.dp),
                            color = FirefoxTheme.colors.freespokeBorderColor,
                        )
                        TextButton(
                            modifier = Modifier.weight(1f),
                            text = allowButtonText.orEmpty(),
                            onClick = it,
                            textColor = FirefoxTheme.colors.dialogButtonsBlueTextColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WhiteListItem(data: String, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = FirefoxTheme.colors.freeSpokeProfileGradientEndColor)
            .border(1.dp, FirefoxTheme.colors.freespokeBorderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(4.dp))
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1F)
                .height(20.dp),
            text = data,
            color = FirefoxTheme.colors.freespokeDescriptionColor,
            style = FirefoxTheme.typography.body1.copy(
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
            ),
        )
        Image(
            modifier = modifier
                .height(16.dp)
                .width(16.dp),
            painter = painterResource(id = R.drawable.ic_close_freespoke_light),
            contentDescription = null,
        )
    }
}
