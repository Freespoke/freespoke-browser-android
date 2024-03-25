@file:Suppress("DEPRECATION")

package org.mozilla.fenix.onboarding.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.text.TextUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import org.mozilla.fenix.compose.button.PrimaryButtonOnboarding
import org.mozilla.fenix.theme.FirefoxTheme


@Composable
fun BaseOnboardingView(
    onDismiss: () -> Unit,
    pageState: OnboardingPageState,
    modifier: Modifier,
    updatedOnboardingState: (UpgradeOnboardingState) -> Unit,
    context: Context?,
) {
    ComposableLifecycle { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            if (shouldSkipScreen(context, pageState.type)) {
                updatedOnboardingState(
                    UpgradeOnboardingState.Notifications
                )
            }
        }
    }
    if (shouldSkipScreen(context, pageState.type)) {
        updatedOnboardingState(
            UpgradeOnboardingState.Notifications
        )
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            createCloseIcon(modifier = Modifier.align(Alignment.End), onDismiss)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                modifier = Modifier.padding(horizontal = 45.dp),
                text = pageState.title,
                color = FirefoxTheme.colors.onboardingTextColor,
                textAlign = TextAlign.Center,
                style = FirefoxTheme.typography.headLine3,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                modifier = Modifier.padding(horizontal = 30.dp),
                text = pageState.description,
                color = FirefoxTheme.colors.textSecondary,
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

                LaunchedEffect(pageState, LocalLifecycleOwner.current) {
                    pageState.onRecordImpressionEvent()
                }
            }
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .background(FirefoxTheme.colors.layerOnboarding),
        ) {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = FirefoxTheme.colors.dividerColor,
            )

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButtonOnboarding(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                text = pageState.primaryButtonText,
                onClick = {
                    updatedOnboardingState(
                        getNextStep(pageState.type),
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
                            updatedOnboardingState(
                                when (pageState.type) {
                                    UpgradeOnboardingState.DefaultBrowser, UpgradeOnboardingState.DefaultBrowserShow -> UpgradeOnboardingState.Notifications
                                    else -> UpgradeOnboardingState.CompleteOnboarding
                                },
                            )
                        },
                    text = pageState.secondaryButtonText,
                    color = FirefoxTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                    style = FirefoxTheme.typography.subtitle1,
                )
            }
        }
    }
}

fun shouldSkipScreen(context: Context?, type: UpgradeOnboardingState?): Boolean {
    return if (type == UpgradeOnboardingState.DefaultBrowser || type == UpgradeOnboardingState.DefaultBrowserShow) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"))
    val resolveInfo: ResolveInfo? =
        context?.packageManager?.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        var defaultBrowserPkg: String? = null
        if (resolveInfo?.activityInfo != null) {
            defaultBrowserPkg = resolveInfo.activityInfo.packageName
        }
        return TextUtils.equals(context?.packageName, defaultBrowserPkg)
    } else {
        false
    }
}

fun getNextStep(type: UpgradeOnboardingState?): UpgradeOnboardingState {
    return when(type) {
        UpgradeOnboardingState.DefaultBrowser, UpgradeOnboardingState.DefaultBrowserShow -> UpgradeOnboardingState.DefaultBrowserShow
        UpgradeOnboardingState.Notifications, UpgradeOnboardingState.NotificationsSetup -> UpgradeOnboardingState.NotificationsSetup
        else -> UpgradeOnboardingState.CompleteOnboarding
    }
}

@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
