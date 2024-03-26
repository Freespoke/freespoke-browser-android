package mozilla.components.browser.engine.gecko.util

import android.content.Context
import androidx.annotation.RawRes
import mozilla.components.browser.engine.gecko.R
import org.mozilla.geckoview.GeckoSession
import java.io.BufferedReader
import java.io.InputStreamReader

class BlockListHandler(private val context: Context) {

    var easyList = emptyList<String>()
    var pplyoyoList = emptyList<String>()
    var urlhausList = emptyList<String>()
    val whiteList
        get() = context.getSharedPreferences(WHITE_LIST_PREF_NAME, Context.MODE_PRIVATE)
            .getString(WHITE_LIST_KEY, "")?.split(",")?.filter { it.isNotBlank() }.orEmpty()

    init {
        easyList = getEasyList(R.raw.easylist)
        pplyoyoList = getEasyList(R.raw.pplyoyo)
        urlhausList = getEasyList(R.raw.urlhausfilter)
    }

    private fun getEasyList(@RawRes rawRes: Int): List<String> {
        val list = mutableListOf<String>()
        val buildingInfo = context.resources.openRawResource(rawRes)
        val reader = BufferedReader(InputStreamReader(buildingInfo))
        var myLine: String
        while (reader.readLine().also { myLine = it.orEmpty() } != null) list.add(myLine)
        return list
    }

    fun isAdsRequest(request: GeckoSession.NavigationDelegate.LoadRequest) =
        (easyList.any { request.uri.contains(it, true) }
            || pplyoyoList.any { request.uri.contains(it, true) }
            || urlhausList.any { request.uri.contains(it, true) })
            && whiteList.none { request.uri.contains(it, true) }

    companion object {
        private const val WHITE_LIST_PREF_NAME = "whitelist_preferences"
        private const val WHITE_LIST_KEY = "white_list"
    }
}

