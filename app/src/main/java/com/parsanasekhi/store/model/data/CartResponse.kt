package com.parsanasekhi.store.model.data

data class CartResponse(
    val success: Boolean,
    val message: String,
    val stock: Int
)