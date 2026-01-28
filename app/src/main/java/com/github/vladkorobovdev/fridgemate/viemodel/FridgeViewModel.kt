package com.github.vladkorobovdev.fridgemate.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.vladkorobovdev.fridgemate.data.Product
import com.github.vladkorobovdev.fridgemate.data.ProductDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FridgeViewModel(private val dao: ProductDao) : ViewModel() {
    val allProducts: StateFlow<List<Product>> = dao.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProduct(name: String, date: Long, ean: String? = null) {
        viewModelScope.launch {
            dao.insertProduct(Product(name = name, expirationDate = date, eanCode = ean))
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            dao.deleteProduct(product)
        }
    }
}

class FridgeViewModelFactory(private val dao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FridgeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FridgeViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}