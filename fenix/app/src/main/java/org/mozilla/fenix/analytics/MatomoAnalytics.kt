package org.mozilla.fenix.analytics

import org.mozilla.fenix.BuildConfig

class MatomoAnalytics {

    companion object {

        const val PROD_ID = 6
        const val STAGING_ID = 7

        //Categories
        const val ENTRY = "app entry"
        const val MENU = "app menu"
        const val HOME = "app home"
        const val TABS = "app tabs"
        const val SHARE = "app share"

        //Actions
        const val ACTION_ENTRY = "app entry"
        const val TAB_MENU_NEWS_CLICKED = "app menu tab click - news"
        const val TAB_MENU_SHOP_CLICKED = "app menu tab click - shop"
        const val TAB_MENU_HOME_CLICKED = "app menu tab click - home"
        const val TAB_MENU_TABS_CLICKED = "app menu tab click - tabs"
        const val TAB_MENU_ELECTION_CLICKED = "app menu tab click - election"
        const val TAB_MENU_SETTINGS_CLICKED = "app menu tab click - settings"
        const val HOME_SEARCH = "app home search"
        const val HOME_BOOKMARKS = "app home my bookmarks click"
        const val HOME_TRENDING_NEWS = "app home trending news story view summary click"
        const val HOME_RECENTLY_ITEMS = "app home recently viewed click"
        const val HOME_SHOP = "app home shop usa storte product click"
        const val HOME_FREESPOKEWAY_CLICK = "app home the freespoke way click - "
        const val APP_SHARE_FROM_MENU = "app share from menu"
        const val APP_TABS_CLOSE_MENU = "app tabs close tabs menu"
        const val APP_TABS_NEW_TAB = "app tabs new tab click"
        const val APP_TABS_CLOSE_ALL = "app tabs close all tabs click"
        const val APP_TABS_PRIVATE = "app tabs private browsing click"
        const val APP_TABS_REGULAR = "app tabs regular browsing click"

        //Names
        const val OPEN = "open"
        const val CLICK = "click"
        const val SEARCH = "search"

        fun getTrackerId(): Int {
            return if (BuildConfig.DEBUG) {
                STAGING_ID
            } else {
                PROD_ID
            }
        }
    }
}
