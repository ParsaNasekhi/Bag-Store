package com.parsanasekhi.store.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.Nullable

data class ProductResponse(
    val products: List<Product>,
    val success: Boolean
)

@Entity(tableName = "product_table")
data class Product(
    val category: String,
    val detailText: String,
    val imgUrl: String,
    val material: String,
    val name: String,
    val price: String,
    @PrimaryKey
    val productId: String,
    val soldItem: String,
    val tags: String
)