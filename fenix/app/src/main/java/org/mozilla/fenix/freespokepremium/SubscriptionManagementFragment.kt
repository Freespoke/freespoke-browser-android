/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.freespokepremium

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import org.mozilla.fenix.components.billing.Billing
import org.mozilla.fenix.components.components
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.hideToolbar
import org.mozilla.fenix.theme.FirefoxTheme
import timber.log.Timber

class SubscriptionManagementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                FirefoxTheme {
                    val profileState = components.freespokeProfileStore.state

                    SubscriptionManagementPage(
                        onUpgradePlan = {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(Billing.GOOGLE_PLAY_SUBSCRIPTION_URL)
                            context.startActivity(intent)
                        },
                        onCancelPlan = { isNativeSubscription ->
                            if (isNativeSubscription) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(Billing.GOOGLE_PLAY_SUBSCRIPTION_URL)
                                context.startActivity(intent)
                            } else {
                                profileState.profile?.let {
                                    val url = it.manageSubscriptionLink
                                    //todo open web wrapper
                                    (context as HomeActivity).openToBrowserAndLoad(
                                        searchTermOrURL = url,
                                        newTab = true,
                                        from = BrowserDirection.FromGlobal,
                                    )
                                    Log.d("manageSubscription", url)
                                }
                            }
                        },
                        onLogin = {
                            (context as HomeActivity).components.strictMode.allowDiskReads()
                            (context as HomeActivity).startLoginFlow { success ->
                                if (success) {
                                    Timber.d("Success sign in")
                                }
                            }
                        },
                        onDismiss = {
                            findNavController().popBackStack()
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
