package com.parsanasekhi.store.model.repository.cart

import com.parsanasekhi.store.model.data.CartResponse
import com.parsanasekhi.store.model.data.UserCartInfo
import ir.dunijet.dunibazaar.model.data.CheckOut
import ir.dunijet.dunibazaar.model.data.SubmitOrder

interface CartRepository {

    suspend fun addToCart(productId: String): CartResponse
    suspend fun getUserCart(): UserCartInfo
    suspend fun removeFromCart(productId: String): Boolean
    suspend fun submitOrder(address: String, postalCode: String): SubmitOrder
    suspend fun checkOut(orderId: String): CheckOut
    fun setOrderId(orderId: String)
    fun getOrderId(): String
    fun setPurchaseStatus(status: Int)
    fun getPurchaseStatus(): Int

}