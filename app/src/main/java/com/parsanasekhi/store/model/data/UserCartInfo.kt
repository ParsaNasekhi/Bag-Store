package com.parsanasekhi.store.model.data

data class UserCartInfo(
    val success: Boolean,
    val productList: List<ProductCart>,
    val totalPrice: Int
)

data class ProductCart(
    val category: String,
    val detailText: String,
    val imgUrl: String,
    val material: String,
    val name: String,
    val price: String,
    val productId: String,
    val soldItem: String,
    val tags: String,
    val quantity: String?
)