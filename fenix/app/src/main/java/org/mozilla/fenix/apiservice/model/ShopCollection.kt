package org.mozilla.fenix.apiservice.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShopCollection(@Json(name = "collections") val collections: List<Shop>)

@JsonClass(generateAdapter = true)
data class Shop(@Json(name = "id") val id: String,
                @Json(name = "title") val title: String,
                @Json(name = "thumbnail") val thumbnail: String?,
                @Json(name = "url") val url: String)
