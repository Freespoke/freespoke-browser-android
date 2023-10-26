package org.mozilla.fenix.apiservice.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class TrendingNews(
    @Json(name = "url") val url: String,
    @Json(name = "name") val name: String,
    @Json(name = "updated_at") val updatedAt: Date?,
    @Json(name = "sources") val sources: Int,
    @Json(name = "bias_left") val leftCount: Int,
    @Json(name = "bias_middle") val middleCount: Int,
    @Json(name = "bias_right") val rightCount: Int,
    @Json(name = "main_image") val image: NewsImage,
    @Json(name = "publisher_icons") val icons: List<String>
)

@JsonClass(generateAdapter = true)
data class NewsImage(
    @Json(name = "url") val url: String,
    @Json(name = "attribution") val attribution: String
)
