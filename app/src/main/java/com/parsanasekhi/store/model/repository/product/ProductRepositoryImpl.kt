package com.parsanasekhi.store.model.repository.product

import com.parsanasekhi.store.model.data.Ad
import com.parsanasekhi.store.model.data.Product
import com.parsanasekhi.store.model.db.ProductDao
import com.parsanasekhi.store.model.net.ApiService

class ProductRepositoryImpl(
    private val apiService: ApiService,
    private val productDao: ProductDao
) : ProductRepository {

    override suspend fun getProducts(isInternetConnected: Boolean): List<Product> {
        if (isInternetConnected) {
            val productResponse = apiService.getAllProducts()
            if (productResponse.success) {
                productDao.insertOrUpdate(productResponse.products)
                return productResponse.products
            }
        } else {
            return productDao.getAll()
        }
        return listOf()
    }

    override suspend fun getAds(isInternetConnected: Boolean): List<Ad> {
        if (isInternetConnected) {
            val adsResponse = apiService.getAllAds()
            if (adsResponse.success) return adsResponse.ads
        }
        return listOf()
    }

    override suspend fun getProductsByCategory(category: String): List<Product> {
        return productDao.getAllByCategory(category)
    }

    override suspend fun getProductById(productId: String): Product {
        return productDao.getProductById(productId)
    }

}