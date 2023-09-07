package org.mozilla.fenix.apiservice.model

data class ShopCollection(val collections: List<Shop>)

data class Shop(val id: String,
                val title: String,
                val thumbnail: String?,
                val url: String)
