/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.accompanist.insets.ProvideWindowInsets
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.R
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.navigateToNotificationAppsSettings
import org.mozilla.fenix.ext.openSetDefaultBrowserOption
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.onboarding.view.OnboardingAppSettings
import org.mozilla.fenix.onboarding.view.UpgradeOnboarding
import org.mozilla.fenix.onboarding.viewmodel.AccountViewModel
import org.mozilla.fenix.theme.FirefoxTheme
import timber.log.Timber
/**
 * Dialog displaying a welcome and sync sign in onboarding.
 */
class HomeOnboardingDialogFragment : DialogFragment() {

    private val viewModel: AccountViewModel by viewModels {
        AccountViewModel.Factory
    }
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.HomeOnboardingDialogStyle)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        setContent {
            ProvideWindowInsets {
                FirefoxTheme {
                    UpgradeOnboarding(
                        onDismiss = ::onDismiss,
                        onSetupSettingsClick = { setting, action ->
                            when (setting) {
                                OnboardingAppSettings.SetupDefaultBrowser -> {
                                    (context as HomeActivity).openSetDefaultBrowserOption()
                                }
                                OnboardingAppSettings.Notifications -> {
                                    (context as HomeActivity).requestNotificationPermission {
                                        action?.invoke()
                                    }
                                }
                                OnboardingAppSettings.Login -> {
                                    (context as HomeActivity).components.strictMode.allowDiskReads()
                                    (context as HomeActivity).startLoginFlow { success ->
                                        if (success) {
                                            action?.invoke()
                                            Timber.d("Success sign in")
                                        }
                                    }
                                }
                            }
                        },
                        viewModel = viewModel,
                        context = context
                    )
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        context?.settings()?.showHomeOnboardingDialog = false
        (activity as HomeActivity).binding.bottomNavigation.selectedItemId = R.id.action_home

        super.onDismiss(dialog)
    }

    private fun onDismiss() {
        context?.settings()?.showHomeOnboardingDialog = false
        (activity as HomeActivity).binding.bottomNavigation.selectedItemId = R.id.action_home
        dismiss()
    }
}
