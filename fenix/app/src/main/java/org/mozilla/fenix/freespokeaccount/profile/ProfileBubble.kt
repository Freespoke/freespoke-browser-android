package org.mozilla.fenix.freespokeaccount.profile

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.R

class ProfileBubbleWrapperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private var onProfileClick = {}
    private val profileModel: MutableState<ProfileUiModel?> = mutableStateOf(null)

    @Composable
    override fun Content() {
        FirefoxTheme {
            ProfileBubble(
                profileUiModel = profileModel.value,
                onClick = { onProfileClick() },
            )
        }
    }

    fun setOnProfileClick(newOnClick: () -> Unit) {
        onProfileClick = newOnClick
    }

    fun updateProfile(newProfile: ProfileUiModel) {
        profileModel.value = newProfile
    }
}

@Composable
fun ProfileBubble(
    profileUiModel: ProfileUiModel?,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(CircleShape)
            .clickable(enabled = isEnabled) {
                onClick()
            }
            .border(1.dp, FirefoxTheme.colors.dividerColor, CircleShape)
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (profileUiModel == null) {
            Icon(
                painter = painterResource(id = R.drawable.ic_freespoke_avatar),
                contentDescription = null,
                tint = FirefoxTheme.colors.iconPrimary,
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            ) {
                if (profileUiModel.hasPremium) {
                    Image(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(id = R.drawable.ic_premium_small),
                        contentDescription = null,
                    )
                }

                Text(
                    text = profileUiModel.shortName,
                    color = FirefoxTheme.colors.textPrimary,
                    style = FirefoxTheme.typography.button
                )
            }

        }
    }
}
