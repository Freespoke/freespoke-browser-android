package org.mozilla.fenix.apiservice.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuickLinkObject(@Json(name = "label") val label: String,
                           @Json(name = "data") val data: List<QuickLink>)
