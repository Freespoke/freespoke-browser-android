package org.mozilla.fenix.apiservice.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuickLink(@Json(name = "category") val category: String,
                     @Json(name = "categoryIcon") val categoryIcon: String,
                     @Json(name = "title") val title: String,
                     @Json(name = "url") val url: String)
