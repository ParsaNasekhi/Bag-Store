package com.parsanasekhi.store.ui.features.category

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parsanasekhi.store.model.data.Product
import com.parsanasekhi.store.model.repository.product.ProductRepository
import com.parsanasekhi.store.util.coroutineExceptionHandler
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    val productsList = mutableStateOf<List<Product>>(listOf())

    fun loadDataFromRepository(category: String) {

        viewModelScope.launch(coroutineExceptionHandler) {
            productsList.value = productRepository.getProductsByCategory(category)
        }

    }

}