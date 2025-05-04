package com.example.umkm_sekitar.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umkm_sekitar.data.model.Store
import com.example.umkm_sekitar.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _store = MutableStateFlow<Store?>(null)
    val store: StateFlow<Store?> = _store

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _cartAdded = MutableSharedFlow<Unit>()
    val cartAdded: SharedFlow<Unit> = _cartAdded

    private val _isAddingToCart = MutableStateFlow(false)
    val isAddingToCart: StateFlow<Boolean> = _isAddingToCart

    private val _addToCartError = MutableStateFlow<String?>(null)
    val addToCartError: StateFlow<String?> = _addToCartError

    fun loadStoreById(storeId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                storeRepository.getStoreById(storeId).collect { result ->
                    _store.value = result
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun addToCart(userId: String, storeId: String, productId: String, quantity: Int) {
        _isAddingToCart.value = true
        _addToCartError.value = null
        viewModelScope.launch {
            storeRepository.addToCart(userId, productId, storeId, quantity)
                .catch { e ->
                    _addToCartError.value = e.message
                    _isAddingToCart.value = false
                }
                .collect { success ->
                    _isAddingToCart.value = false
                    if (success) {
                        _cartAdded.emit(Unit)
                    } else {
                        _addToCartError.value = "Gagal menambahkan ke keranjang"
                    }
                }
        }
    }
}

