package com.parsanasekhi.store.model.repository.product

import com.parsanasekhi.store.model.data.Ad
import com.parsanasekhi.store.model.data.Product

interface ProductRepository {

    suspend fun getProducts(isInternetConnected: Boolean): List<Product>

    suspend fun getAds(isInternetConnected: Boolean): List<Ad>

    suspend fun getProductsByCategory(category: String): List<Product>

    suspend fun getProductById(productId: String): Product

}