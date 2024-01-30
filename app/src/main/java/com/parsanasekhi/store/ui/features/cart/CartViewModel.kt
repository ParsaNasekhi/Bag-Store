package com.parsanasekhi.store.ui.features.cart

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parsanasekhi.store.model.data.ProductCart
import com.parsanasekhi.store.model.repository.cart.CartRepository
import com.parsanasekhi.store.model.repository.user.UserRepository
import com.parsanasekhi.store.util.coroutineExceptionHandler
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val products = mutableStateOf(listOf<ProductCart>())
    val totalPrice = mutableStateOf("0")

    fun loadCartData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = cartRepository.getUserCart()
            products.value = result.productList
            calculateTotalPrice()
        }
    }

    fun addToCart(productId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = cartRepository.addToCart(productId)
            if (result.success) {
                loadCartData()
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = cartRepository.removeFromCart(productId)
            if (result) {
                loadCartData()
            }
        }
    }

    private fun calculateTotalPrice() {
        var totalPrice = 0
        products.value.forEach {
            totalPrice += it.price.toInt() * ((it.quantity ?: "0").toInt())
        }
        this.totalPrice.value = totalPrice.toString()
    }


    fun purchaseAll(address: String, postalCode: String, successEvent: (Boolean, String) -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = cartRepository.submitOrder(address, postalCode)
            successEvent(result.success, result.paymentLink)
        }
    }

    fun setPaymentStatus(status: Int) {
        cartRepository.setPurchaseStatus(status)
    }

    fun getUserLocation(): Pair<String, String> {
        return userRepository.getUserLocation()
    }

    fun setUserLocation(address: String, postalCode: String) {
        userRepository.saveUserLocation(address, postalCode)
    }

}