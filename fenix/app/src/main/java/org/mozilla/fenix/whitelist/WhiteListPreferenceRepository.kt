package org.mozilla.fenix.whitelist

import android.content.Context
import androidx.core.content.edit
import org.mozilla.fenix.FenixApplication

class WhiteListPreferenceRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(WHITE_LIST_PREF_NAME, Context.MODE_PRIVATE)
    fun getWhiteList(): List<String> {
        return sharedPreferences.getString(WHITE_LIST_KEY, "")?.lowercase()?.split(",")?.filter { it.isNotBlank() }.orEmpty()
    }

    fun writeWhiteList(list: List<String>) {
        sharedPreferences.edit {
            putString(WHITE_LIST_KEY, StringBuffer().apply {
                list.forEach {
                    this.append(it.lowercase())
                    this.append(",")
                }
            }.toString())
        }

    }

    companion object {
        private const val WHITE_LIST_PREF_NAME = "whitelist_preferences"
        private const val WHITE_LIST_KEY = "white_list"
    }

}
