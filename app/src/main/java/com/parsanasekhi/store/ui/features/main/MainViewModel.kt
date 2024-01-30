package com.parsanasekhi.store.ui.features.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parsanasekhi.store.model.data.Ad
import com.parsanasekhi.store.model.data.Product
import com.parsanasekhi.store.model.repository.cart.CartRepository
import com.parsanasekhi.store.model.repository.product.ProductRepository
import com.parsanasekhi.store.util.coroutineExceptionHandler
import ir.dunijet.dunibazaar.model.data.CheckOut
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val isInternetConnected: Boolean
) : ViewModel() {

    val dataProducts = mutableStateOf<List<Product>>(listOf())
    val dataAds = mutableStateOf<List<Ad>>(listOf())
    val showProgressBar = mutableStateOf(false)
    val badgeNumber = mutableStateOf("0")

    val showPaymentResultDialog = mutableStateOf(false)
    val checkoutData = mutableStateOf(CheckOut(null, null))

    init {
        refreshDataFromServer()
    }

    fun getCheckoutData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = cartRepository.checkOut(cartRepository.getOrderId())
            if (result.success!!) {
                checkoutData.value = result
                showPaymentResultDialog.value = true
            }
        }
    }

    fun getPaymentStatus(): Int {
        return cartRepository.getPurchaseStatus()
    }

    fun setPaymentStatus(status: Int) {
        cartRepository.setPurchaseStatus(status)
    }

    private fun refreshDataFromServer() {

        viewModelScope.launch(coroutineExceptionHandler) {

            if (isInternetConnected)
                showProgressBar.value = true

            delay(1200)

            val productsFromServer = async { productRepository.getProducts(isInternetConnected) }
            val adsFromServer = async { productRepository.getAds(isInternetConnected) }

            updateData(productsFromServer.await(), adsFromServer.await())

            showProgressBar.value = false

        }

    }

    private fun updateData(products: List<Product>, ads: List<Ad>) {
        dataProducts.value = products
        dataAds.value = ads
        println()
    }

    fun getBadgeNumber() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = cartRepository.getUserCart()
            var counter = 0
            if (result.success) {
                result.productList.forEach {
                    counter += (it.quantity ?: "0").toInt()
                }
            }
            badgeNumber.value = counter.toString()
        }
    }

}