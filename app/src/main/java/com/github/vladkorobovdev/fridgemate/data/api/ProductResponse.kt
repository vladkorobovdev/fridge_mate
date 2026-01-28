package com.github.vladkorobovdev.fridgemate.data.api

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val product: ProductNetwork? = null,
    val status: Int = 0
)

data class ProductNetwork(
    @SerializedName("product_name")
    val productName: String? = null,

    @SerializedName("image_url")
    val imageUrl: String? = null
)