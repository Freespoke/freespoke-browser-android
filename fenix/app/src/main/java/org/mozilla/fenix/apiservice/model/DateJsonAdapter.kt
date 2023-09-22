package org.mozilla.fenix.apiservice.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateJsonAdapter: JsonAdapter<Date?>() {
    @FromJson
    override fun fromJson(reader: JsonReader): Date? {
        val value = reader.nextString()
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault()).parse(value)
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) {
        value?.let {
            writer.value(SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault()).format(value))
        }
    }
}
