package com.parsanasekhi.store.model.data

data class AdsResponse(
    val ads: List<Ad>,
    val success: Boolean
)

data class Ad(
    val adId: String,
    val imageURL: String,
    val productId: String
)