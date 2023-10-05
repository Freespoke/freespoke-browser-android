package org.mozilla.fenix.freespokehome

import android.content.Context
import android.content.Intent
import android.os.StrictMode
import android.provider.Settings
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import mozilla.appservices.fxaclient.Config
import mozilla.appservices.places.BookmarkRoot
import mozilla.components.browser.menu.view.MenuButton
import mozilla.components.service.glean.private.NoExtras
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.GleanMetrics.Events
import org.mozilla.fenix.GleanMetrics.HomeScreen
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.NavGraphDirections
import org.mozilla.fenix.R
import org.mozilla.fenix.analytics.MatomoAnalytics
import org.mozilla.fenix.components.FenixSnackbar
import org.mozilla.fenix.components.accounts.AccountState
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.nav
import org.mozilla.fenix.ext.navigateToNotificationAppsSettings
import org.mozilla.fenix.ext.openSetDefaultBrowserOption
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.home.HomeMenu
import org.mozilla.fenix.settings.SupportUtils
import org.mozilla.fenix.settings.SupportUtils.getFreespokeSupportURLPage
import org.mozilla.fenix.settings.deletebrowsingdata.deleteAndQuit
import org.mozilla.fenix.theme.ThemeManager
import org.mozilla.fenix.whatsnew.WhatsNew
import java.lang.ref.WeakReference
import kotlin.math.acos


class FreespokeHomeMenuBuilder (
        private val view: View,
        private val context: Context,
        private val lifecycleOwner: LifecycleOwner,
        private val homeActivity: HomeActivity,
        private val navController: NavController,
        private val menuButton: WeakReference<MenuButton>? = null,
        private val hideOnboardingIfNeeded: () -> Unit,
    ) {

        /**
         * Builds the [HomeMenu].
         */
        fun build() {
            HomeMenu(
                lifecycleOwner = lifecycleOwner,
                context = context,
                onItemTapped = ::onItemTapped,
                onHighlightPresent = { menuButton?.get()?.setHighlight(it) },
                onMenuBuilderChanged = { menuButton?.get()?.menuBuilder = it },
            )

            menuButton?.get()?.setColorFilter(
                ContextCompat.getColor(
                    context,
                    ThemeManager.resolveAttribute(R.attr.textPrimary, context),
                ),
            )
        }

        /**
         * Callback invoked when a menu item is tapped on.
         */
        @Suppress("LongMethod", "ComplexMethod")
        @VisibleForTesting(otherwise = PRIVATE)
        internal fun onItemTapped(item: HomeMenu.Item) {
            if (item !is HomeMenu.Item.DesktopMode) {
                hideOnboardingIfNeeded()
            }

            when (item) {
                HomeMenu.Item.Settings -> {
                    org.mozilla.fenix.GleanMetrics.HomeMenu.settingsItemClicked.record(NoExtras())

                    navController.nav(
                        getFragmentId(),
                        FreespokeHomeFragmentDirections.actionGlobalSettingsFragment(),
                    )
                }
                HomeMenu.Item.CustomizeHome -> {
                    HomeScreen.customizeHomeClicked.record(NoExtras())

                    navController.nav(
                        getFragmentId(),
                        FreespokeHomeFragmentDirections.actionGlobalHomeSettingsFragment(),
                    )
                }
                is HomeMenu.Item.SyncAccount -> {
                    navController.nav(
                        getFragmentId(),
                        when (item.accountState) {
                            AccountState.AUTHENTICATED ->
                                FreespokeHomeFragmentDirections.actionGlobalAccountSettingsFragment()
                            AccountState.NEEDS_REAUTHENTICATION ->
                                FreespokeHomeFragmentDirections.actionGlobalAccountProblemFragment()
                            AccountState.NO_ACCOUNT ->
                                FreespokeHomeFragmentDirections.actionGlobalTurnOnSync()
                        },
                    )
                }
                HomeMenu.Item.ManageAccountAndDevices -> {
                    homeActivity.openToBrowserAndLoad(
                        searchTermOrURL =
                        if (context.settings().allowDomesticChinaFxaServer) {
                            Config.Server.CHINA.contentUrl + "/settings"
                        } else {
                            Config.Server.RELEASE.contentUrl + "/settings"
                        },
                        newTab = true,
                        from = BrowserDirection.FromGlobal,
                    )
                }
                HomeMenu.Item.Bookmarks -> {
                    navController.nav(
                        getFragmentId(),
                        NavGraphDirections.actionGlobalBookmarkFragment(BookmarkRoot.Mobile.id),
                    )
                }
                HomeMenu.Item.History -> {
                    navController.nav(
                        getFragmentId(),
                        FreespokeHomeFragmentDirections.actionGlobalHistoryFragment(),
                    )
                }
                HomeMenu.Item.Downloads -> {
                    navController.nav(
                        getFragmentId(),
                        NavGraphDirections.actionGlobalDownloadsFragment(),
                    )
                }
                HomeMenu.Item.Help -> {
                    homeActivity.openToBrowserAndLoad(
                        searchTermOrURL = getFreespokeSupportURLPage(SupportUtils.FreespokeSupportPage.HOME),
                        newTab = true,
                        from = BrowserDirection.FromGlobal,
                    )
                }
                HomeMenu.Item.WhatsNew -> {
                    WhatsNew.userViewedWhatsNew(context)
                    Events.whatsNewTapped.record(NoExtras())

                    homeActivity.openToBrowserAndLoad(
                        searchTermOrURL = SupportUtils.getWhatsNewUrl(context),
                        newTab = true,
                        from = BrowserDirection.FromGlobal,
                    )
                }
                HomeMenu.Item.Quit -> {
                    // We need to show the snackbar while the browsing data is deleting (if "Delete
                    // browsing data on quit" is activated). After the deletion is over, the snackbar
                    // is dismissed.
                    deleteAndQuit(
                        activity = homeActivity,
                        coroutineScope = lifecycleOwner.lifecycleScope,
                        snackbar = FenixSnackbar.make(
                            view = view,
                            isDisplayedWithBrowserToolbar = false,
                        ),
                    )
                }
                HomeMenu.Item.ReconnectSync -> {
                    navController.nav(
                        getFragmentId(),
                        FreespokeHomeFragmentDirections.actionGlobalAccountProblemFragment(),
                    )
                }
                HomeMenu.Item.Extensions -> {
                    navController.nav(
                        getFragmentId(),
                        FreespokeHomeFragmentDirections.actionGlobalAddonsManagementFragment(),
                    )
                }
                is HomeMenu.Item.DesktopMode -> {
                    context.settings().openNextTabInDesktopMode = item.checked
                }
                HomeMenu.Item.AboutFreespoke -> {
                    homeActivity.openToBrowserAndLoad(
                        searchTermOrURL = context.getString(R.string.about_freespoke_url),
                        newTab = true,
                        from = BrowserDirection.FromGlobal,
                    )
                }
                HomeMenu.Item.FreespokeBlog -> {
                    homeActivity.openToBrowserAndLoad(
                        searchTermOrURL = context.getString(R.string.freespoke_blog_url),
                        newTab = true,
                        from = BrowserDirection.FromGlobal,
                    )
                }
                HomeMenu.Item.FreespokePremium -> {
                    homeActivity.openToBrowserAndLoad(
                        searchTermOrURL = context.getString(R.string.get_premium_url),
                        newTab = true,
                        from = BrowserDirection.FromGlobal,
                    )
                }
                HomeMenu.Item.GetSupport -> {
                    homeActivity.openToBrowserAndLoad(
                        searchTermOrURL = context.getString(R.string.support_url),
                        newTab = true,
                        from = BrowserDirection.FromGlobal,
                    )
                }
                HomeMenu.Item.MakeDefaultFreespoke -> {
                    (context as HomeActivity).openSetDefaultBrowserOption()
                }
                HomeMenu.Item.Notifications -> {
                    homeActivity.components.strictMode.resetAfter(StrictMode.allowThreadDiskReads()) {
                        (context as HomeActivity).navigateToNotificationAppsSettings()
                    }
                }
                HomeMenu.Item.ShareFreespoke -> {
                    (homeActivity.application as FenixApplication).trackEvent(MatomoAnalytics.SHARE,
                        MatomoAnalytics.APP_SHARE_FROM_MENU, MatomoAnalytics.CLICK)
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "https://freespoke.com/")
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            }
        }

    private fun getFragmentId(): Int? {
        return homeActivity.getFragmentId()
    }
}
