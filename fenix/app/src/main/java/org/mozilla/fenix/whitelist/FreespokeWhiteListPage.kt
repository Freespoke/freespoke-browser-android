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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.mozilla.fenix.R
import org.mozilla.fenix.freespokeaccount.FreespokeProfileListItem
import org.mozilla.fenix.freespokeaccount.FreespokeProfileListItemType
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.whitelist.store.WhiteListData

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
                text = stringResource(id = R.string.browser_menu_back),
                color = FirefoxTheme.colors.textPrimary,
                style = FirefoxTheme.typography.headline7.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        FreespokeProfileListItem(
            type = FreespokeProfileListItemType.Toggle(
                adsBlockEnabled,
                onToggled = {
                    viewModel.updateAdsBlockState(it)
                    onAdBlockStatusChange(it)
                },
            ),
            text = stringResource(id = R.string.text_block_ads),
            iconPainter = painterResource(id = R.drawable.ic_settings_drawer),
        )
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
                            viewModel.removeItem(item)
                        },
                    ),
                )
            }
        }
    }
}

@Composable
fun WhiteListItem(data: WhiteListData, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FirefoxTheme.colors.dividerColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(4.dp))
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1F)
                .height(20.dp),
            text = data.urlDomain,
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
