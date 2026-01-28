package com.github.vladkorobovdev.fridgemate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.vladkorobovdev.fridgemate.data.Product
import com.github.vladkorobovdev.fridgemate.data.ProductDao
import com.github.vladkorobovdev.fridgemate.data.api.OpenFoodFactsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FridgeViewModel(private val dao: ProductDao) : ViewModel() {
    private val api = OpenFoodFactsApi.create()

    val allProducts: StateFlow<List<Product>> = dao.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProduct(name: String, date: Long, ean: String? = null, image: String? = null) {
        viewModelScope.launch {
            dao.insertProduct(Product(
                name = name,
                expirationDate = date,
                eanCode = ean,
                imagePath = image
            ))
        }
    }
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            dao.deleteProduct(product)
        }
    }

    fun searchProductByBarcode(barcode: String, onResult: (String?, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getProduct(barcode)
                }

                if (response.status == 1 && response.product != null) {
                    onResult(response.product.productName, response.product.imageUrl)
                } else {
                    onResult(null, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null, null)
            }
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