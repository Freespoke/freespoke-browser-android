/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.browser.menu.BrowserMenuBuilder
import mozilla.components.browser.menu.BrowserMenuHighlight
import mozilla.components.browser.menu.BrowserMenuItem
import mozilla.components.browser.menu.ext.getHighlight
import mozilla.components.browser.menu.item.BrowserMenuDivider
import mozilla.components.browser.menu.item.BrowserMenuHighlightableItem
import mozilla.components.browser.menu.item.BrowserMenuImageSwitch
import mozilla.components.browser.menu.item.BrowserMenuImageText
import mozilla.components.concept.sync.AccountObserver
import mozilla.components.concept.sync.AuthType
import mozilla.components.concept.sync.OAuthAccount
import mozilla.components.support.ktx.android.content.getColorFromAttr
import org.mozilla.fenix.R
import org.mozilla.fenix.components.accounts.AccountState
import org.mozilla.fenix.components.accounts.FenixAccountManager
import org.mozilla.fenix.components.toolbar.BrowserMenuSignIn
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.nimbus.FxNimbus
import org.mozilla.fenix.theme.ThemeManager

@Suppress("LargeClass", "LongMethod")
class HomeMenu(
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val onItemTapped: (Item) -> Unit = {},
    private val onMenuBuilderChanged: (BrowserMenuBuilder) -> Unit = {},
    private val onHighlightPresent: (BrowserMenuHighlight) -> Unit = {},
) {
    sealed class Item {
        object Bookmarks : Item()
        object History : Item()
        object Downloads : Item()
        object Extensions : Item()
        data class SyncAccount(val accountState: AccountState) : Item()

        /**
         * A button item to open up the settings page of FxA, shown up in mozilla online builds.
         */
        object ManageAccountAndDevices : Item()
        object WhatsNew : Item()
        object Help : Item()
        object CustomizeHome : Item()
        object Settings : Item()
        object Quit : Item()
        object ReconnectSync : Item()
        object AboutFreespoke: Item()
        object FreespokeBlog: Item()
        object ShareFreespoke: Item()
        object GetSupport: Item()
        object MakeDefaultFreespoke: Item()
        object Notifications: Item()
        object FreespokePremium: Item()
        data class DesktopMode(val checked: Boolean) : Item()
    }

    private val primaryTextColor = ThemeManager.resolveAttribute(R.attr.textPrimary, context)
    private val syncDisconnectedColor =
        ThemeManager.resolveAttribute(R.attr.syncDisconnected, context)
    private val syncDisconnectedBackgroundColor =
        context.getColorFromAttr(R.attr.syncDisconnectedBackground)

    private val accountManager = FenixAccountManager(context)

    // 'Reconnect' and 'Quit' items aren't needed most of the time, so we'll only create the if necessary.
    private val reconnectToSyncItem by lazy {
        BrowserMenuHighlightableItem(
            context.getString(R.string.sync_reconnect),
            R.drawable.ic_sync_disconnected,
            iconTintColorResource = syncDisconnectedColor,
            textColorResource = primaryTextColor,
            highlight = BrowserMenuHighlight.HighPriority(
                backgroundTint = syncDisconnectedBackgroundColor,
                canPropagate = false,
            ),
            isHighlighted = { true },
        ) {
            onItemTapped.invoke(Item.ReconnectSync)
        }
    }

    private val quitItem by lazy {
        BrowserMenuImageText(
            context.getString(R.string.delete_browsing_data_on_quit_action),
            R.drawable.mozac_ic_quit,
            primaryTextColor,
        ) {
            onItemTapped.invoke(Item.Quit)
        }
    }

    private fun syncSignInMenuItem(): BrowserMenuItem {
        return BrowserMenuSignIn(primaryTextColor) {
            onItemTapped.invoke(Item.SyncAccount(accountManager.accountState))
        }
    }

    val desktopItem = BrowserMenuImageSwitch(
        imageResource = R.drawable.ic_desktop,
        label = context.getString(R.string.browser_menu_desktop_site),
        initialState = { context.settings().openNextTabInDesktopMode },
    ) { checked ->
        onItemTapped.invoke(Item.DesktopMode(checked))
    }

    @Suppress("ComplexMethod")
    private fun coreMenuItems(): List<BrowserMenuItem> {
        val settings = context.components.settings

        val bookmarksItem = BrowserMenuImageText(
            context.getString(R.string.library_bookmarks),
            R.drawable.ic_bookmark_list,
            primaryTextColor,
        ) {
            onItemTapped.invoke(Item.Bookmarks)
        }

        val historyItem = BrowserMenuImageText(
            context.getString(R.string.library_history),
            R.drawable.ic_history,
            primaryTextColor,
        ) {
            onItemTapped.invoke(Item.History)
        }

        val downloadsItem = BrowserMenuImageText(
            context.getString(R.string.library_downloads),
            R.drawable.ic_download,
            primaryTextColor,
        ) {
            onItemTapped.invoke(Item.Downloads)
        }

        val extensionsItem = BrowserMenuImageText(
            context.getString(R.string.browser_menu_add_ons),
            R.drawable.ic_addons_extensions,
            primaryTextColor,
        ) {
            onItemTapped.invoke(Item.Extensions)
        }

        val helpItem = BrowserMenuImageText(
            context.getString(R.string.browser_menu_help),
            R.drawable.mozac_ic_help,
            primaryTextColor,
        ) {
            onItemTapped.invoke(Item.Help)
        }

        // Use nimbus to set the icon and title.
        val nimbusValidation = FxNimbus.features.nimbusValidation.value()
        val settingsItem = BrowserMenuImageText(
            nimbusValidation.settingsTitle,
            R.drawable.mozac_ic_settings,
            primaryTextColor,
        ) {
            onItemTapped.invoke(Item.Settings)
        }

        val aboutItem = BrowserMenuImageText(
            context.getString(R.string.about),
            R.drawable.ic_about,
            primaryTextColor
        ) {
            onItemTapped.invoke(Item.AboutFreespoke)
        }

        val freespokeBlog = BrowserMenuImageText(
            context.getString(R.string.freespoke_blog),
            R.drawable.ic_freespoke,
            primaryTextColor
        ) {
            onItemTapped.invoke(Item.FreespokeBlog)
        }

        val shareFreespoke = BrowserMenuImageText(
            context.getString(R.string.share_freespoke),
            R.drawable.ic_share,
            primaryTextColor
        ) {
            onItemTapped.invoke(Item.ShareFreespoke)
        }

        val getSupportItem = BrowserMenuImageText(
            context.getString(R.string.get_support),
            R.drawable.ic_support,
            primaryTextColor
        ) {
            onItemTapped.invoke(Item.GetSupport)
        }

        val makeDefaultBrowser = BrowserMenuImageText(
            context.getString(R.string.make_default_browser),
            R.drawable.ic_add_default,
            primaryTextColor
        ) {
            onItemTapped.invoke(Item.MakeDefaultFreespoke)
        }

        val notificationsItem = BrowserMenuImageText(
            context.getString(R.string.manage_notifications),
            R.drawable.ic_add_notifications,
            primaryTextColor
        ) {
            onItemTapped.invoke(Item.Notifications)
        }

        val premiumItem = BrowserMenuImageText(
            context.getString(R.string.get_freespoke_premium),
            R.drawable.ic_person,
            primaryTextColor
        ) {
            onItemTapped.invoke(Item.FreespokePremium)
        }

        val menuItems = listOfNotNull(
            bookmarksItem,
            historyItem,
            downloadsItem,
            extensionsItem,
            BrowserMenuDivider(),
            desktopItem,
            BrowserMenuDivider(),
            helpItem,
            BrowserMenuDivider(),
            aboutItem,
            freespokeBlog,
            shareFreespoke,
            getSupportItem,
            BrowserMenuDivider(),
            makeDefaultBrowser,
            notificationsItem,
            premiumItem,
            BrowserMenuDivider(),
            settingsItem,
            if (settings.shouldDeleteBrowsingDataOnQuit) quitItem else null,
        ).also { items ->
            items.getHighlight()?.let { onHighlightPresent(it) }
        }

        return menuItems
    }

    init {
        val menuItems = coreMenuItems()

        // Report initial state.
        onMenuBuilderChanged(BrowserMenuBuilder(menuItems))

        // Observe account state changes, and update menu item builder with a new set of items.
        context.components.backgroundServices.accountManagerAvailableQueue.runIfReadyOrQueue {
            // This task isn't relevant if our parent fragment isn't around anymore.
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                return@runIfReadyOrQueue
            }
            context.components.backgroundServices.accountManager.register(
                object : AccountObserver {
                    override fun onAuthenticationProblems() {
                        lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            onMenuBuilderChanged(
                                BrowserMenuBuilder(
                                    menuItems,
                                ),
                            )
                        }
                    }

                    override fun onAuthenticated(account: OAuthAccount, authType: AuthType) {
                        lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            onMenuBuilderChanged(
                                BrowserMenuBuilder(
                                    menuItems,
                                ),
                            )
                        }
                    }

                    override fun onLoggedOut() {
                        lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            onMenuBuilderChanged(
                                BrowserMenuBuilder(
                                    menuItems,
                                ),
                            )
                        }
                    }
                },
                lifecycleOwner,
            )
        }
    }
}
