/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokeaccount

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.R
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.hideToolbar
import org.mozilla.fenix.ext.navigateToNotificationAppsSettings
import org.mozilla.fenix.ext.openSetDefaultBrowserOption
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.theme.FirefoxTheme

class FreespokeProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                FirefoxTheme {
                    FreespokeProfilePage(
                        onManagePremium = {
                            findNavController().navigate(
                                FreespokeProfileFragmentDirections.actionFreespokeProfileFragmentToSubscriptionManagementFragment(),
                            )
                        },
                        onSetDefaultBrowser = {
                            (context as HomeActivity).openSetDefaultBrowserOption()
                        },
                        onManageNotifications = {
                            with(context as HomeActivity) {
                                components.strictMode.resetAfter(StrictMode.allowThreadDiskReads()) {
                                    navigateToNotificationAppsSettings()
                                }
                            }
                        },
                        onShare = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "https://freespoke.com/")
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        },
                        onManageDarkMode = {
                            findNavController().navigate(
                                FreespokeProfileFragmentDirections.actionGlobalCustomizationFragment()
                            )
                        },
                        onSupport = {
                            (context as HomeActivity).openToBrowserAndLoad(
                                searchTermOrURL = context.getString(R.string.support_url),
                                newTab = true,
                                from = BrowserDirection.FromGlobal,
                            )
                        },
                        onBack = {
                            findNavController().popBackStack()
                        },
                        onManageWhiteList =  {
                            findNavController().navigate(
                                FreespokeProfileFragmentDirections.actionFreespokeProfileFragmentToFreespokeWhiteListFragment()
                            )
                        },
                        onManageAdBlocking = {
                            context.settings().adsBlockFeatureEnabled = it
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
    }
}
