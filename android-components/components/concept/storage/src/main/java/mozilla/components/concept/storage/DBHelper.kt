package mozilla.components.concept.storage

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray

class DBHelper(context: Context) : SQLiteOpenHelper(context, "RKStorage", null, 1) {

    companion object {
        private const val TABLE_NAME = "catalystLocalStorage"
        private const val COLUMN_KEY = "key"
        private const val COLUMN_VALUE = "value"
        private const val KEY_SAVED_TABS = "savedTabs"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Database creation not needed for reading data
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Database upgrade not needed for reading data
    }

    @SuppressLint("Range")
    fun getSavedTabsData(): List<SavedTab> {
        val savedTabsData = mutableListOf<SavedTab>()
        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.query(
                TABLE_NAME,
                arrayOf(COLUMN_VALUE),
                "$COLUMN_KEY = ?",
                arrayOf(KEY_SAVED_TABS),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                val savedTabsJson = cursor.getString(cursor.getColumnIndex(COLUMN_VALUE))
                val savedTabsArray = JSONArray(savedTabsJson)

                for (i in 0 until savedTabsArray.length()) {
                    val savedTabObj = savedTabsArray.getJSONObject(i)
                    val url = savedTabObj.getString("url")
                    val title = savedTabObj.getString("title")
                    val savedTab = SavedTab(url, title)
                    savedTabsData.add(savedTab)
                }
            }

            cursor.close()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return savedTabsData
    }
}