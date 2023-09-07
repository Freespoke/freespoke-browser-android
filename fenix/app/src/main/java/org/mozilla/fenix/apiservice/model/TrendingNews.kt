package org.mozilla.fenix.apiservice.model

import com.squareup.moshi.Json
import java.util.Date

data class TrendingNews(
    val url: String,
    val name: String,
    @Json(name = "updated_at") val updatedAt: Date?,
    val sources: Int,
    @Json(name = "bias_left") val leftCount: Int,
    @Json(name = "bias_middle") val middleCount: Int,
    @Json(name = "bias_right") val rightCount: Int,
    @Json(name = "main_image") val image: NewsImage,
    @Json(name = "publisher_icons") val icons: List<String>
)

data class NewsImage(
    val url: String,
    val attribution: String
)
