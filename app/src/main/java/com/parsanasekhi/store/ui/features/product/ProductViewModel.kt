package com.parsanasekhi.store.ui.features.product

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parsanasekhi.store.model.data.Comment
import com.parsanasekhi.store.model.repository.cart.CartRepository
import com.parsanasekhi.store.model.repository.comment.CommentRepository
import com.parsanasekhi.store.model.repository.product.ProductRepository
import com.parsanasekhi.store.util.EMPTY_PRODUCT
import com.parsanasekhi.store.util.coroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val commentRepository: CommentRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    val product = mutableStateOf(EMPTY_PRODUCT)
    val comment = mutableStateOf<List<Comment>>(listOf())
    val isAddingProduct = mutableStateOf(false)
    val badgeNumber = mutableStateOf(0)

    fun loadData(productId: String, isInternetConnected: Boolean) {
        loadProductFromCache(productId)
        if (isInternetConnected) {
            loadCommentsFromServer(productId)
            getBadgeNumber()
        }
    }

    private fun loadCommentsFromServer(productId: String) {
        viewModelScope.launch {
            comment.value = commentRepository.getAllComments(productId)
        }
    }

    private fun loadProductFromCache(productId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            product.value = productRepository.getProductById(productId)
        }
    }

    fun addNewComment(productId: String, text: String, showMessage: (String) -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            commentRepository.addNewComment(productId, text, showMessage)
            delay(2000)
            loadCommentsFromServer(productId)
        }
    }

    fun addProductToCart(productId: String, showResult: (String) -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            isAddingProduct.value = true
            val result = cartRepository.addToCart(productId)
            delay(1000)
            isAddingProduct.value = false
            showResult(result.message)
        }
    }

    private fun getBadgeNumber() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = cartRepository.getUserCart()
            var counter = 0
            if (result.success) {
                result.productList.forEach {
                    counter += (it.quantity ?: "0").toInt()
                }
            }
            badgeNumber.value = counter
        }
    }

}